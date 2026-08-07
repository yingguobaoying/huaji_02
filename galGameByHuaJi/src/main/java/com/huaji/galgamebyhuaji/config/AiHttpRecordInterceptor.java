package com.huaji.galgamebyhuaji.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * HTTP 层拦截器，捕获 AI API 的原始请求/响应 JSON。
 * 通过 AiRecordCache 暂存记录。非流式直接缓存完整 JSON；流式通过 AppendInputStream 逐块追加。
 * <p>
 * 记录 ID 通过 ThreadLocal 在 Advisor → Interceptor 之间传递：
 *   1. Advisor 调用 allocate() 获取 ID 并存入 ThreadLocal
 *   2. Interceptor 从 ThreadLocal 读取 ID 作为缓存 key，然后清除 ThreadLocal
 * <p>
 * 定时任务 / 关闭钩子通过 consumeAllCompleted 统一写回数据库。
 */
@Slf4j
public class AiHttpRecordInterceptor implements ClientHttpRequestInterceptor {

    /** ThreadLocal：Advisor 调用前暂存 recordId，拦截器读取后立即清除 */
    private static final ThreadLocal<Long> PENDING_RECORD_ID = new ThreadLocal<>();

    private static final AiRecordCache CACHE = new AiRecordCache();

    /** 后端暂存传送 ID，供 Interceptor 读取 */
    public static final String HEADER_RECORD_ID = "X-Ai-Record-Id";

    /** 在 AiBastServiceImpl 入口调用：分配一个全局唯一的记录 ID */
    public static long allocate() {
        long id = CACHE.allocate();
        PENDING_RECORD_ID.set(id);
        return id;
    }

    /** Advisor 调用 saveUserMsg 时，把 UserAiContext 中的 recordId 写入 ThreadLocal 供拦截器读取 */
    public static void setPendingRecordId(long id) {
        PENDING_RECORD_ID.set(id);
    }

    /** 消费指定 ID 的缓存记录 */
    public static RecordData consume(long id) {
        return CACHE.consume(id);
    }

    public static int pendingCount() { return CACHE.pendingCount(); }
    public static int completedCount() { return CACHE.completedCount(); }

    public static void consumeAllCompleted(Consumer<RecordData> handler) {
        CACHE.consumeAllCompleted(handler);
    }

    public static void cleanupStale(long timeoutMinutes) {
        CACHE.cleanupStale(timeoutMinutes);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        // 1. 从请求 Header 读取 recordId（由 Advisor 在 saveUserMsg 中写入）
        String headerVal = request.getHeaders().getFirst(HEADER_RECORD_ID);
        long recordId = -1;
        if (headerVal != null) {
            try { recordId = Long.parseLong(headerVal); } catch (NumberFormatException ignored) {}
        }
        if (recordId <= 0) {
            // 没有 recordId，直接放行
            return execution.execute(request, body);
        }

        // 3. 捕获请求体 JSON
        String reqContentType = request.getHeaders().getContentType() != null
                ? request.getHeaders().getContentType().toString() : "";
        if (body != null && body.length > 0
                && (reqContentType.contains(MediaType.APPLICATION_JSON_VALUE) || reqContentType.contains("json"))) {
            CACHE.setRequestJson(recordId, new String(body, StandardCharsets.UTF_8));
        }

        // 4. 执行请求
        ClientHttpResponse response = execution.execute(request, body);

        // 5. 判断流式/非流式并缓存响应
        String respContentType = response.getHeaders().getContentType() != null
                ? response.getHeaders().getContentType().toString() : "";
        boolean isStream = respContentType.contains("text/event-stream") || respContentType.contains("stream");

        if (isStream) {
            return new StreamingResponseWrapper(response, recordId, CACHE);
        } else {
            byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
            CACHE.setResponseJson(recordId, new String(responseBody, StandardCharsets.UTF_8));
            return new CachedBodyClientHttpResponse(response, responseBody);
        }
    }

    // ────────────── 数据类 ──────────────

    public static class RecordData {
        String requestJson;
        final StringBuilder responseBuilder = new StringBuilder();
        volatile boolean complete;

        public String getRequestJson() { return requestJson; }
        public String getResponseJson() {
            synchronized (responseBuilder) { return responseBuilder.toString(); }
        }
        public boolean isComplete() { return complete; }
    }

    // ────────────── 缓存 ──────────────

    static class AiRecordCache {
        private final AtomicLong idGen = new AtomicLong(System.currentTimeMillis());
        private final ConcurrentMap<Long, RecordData> cache = new ConcurrentHashMap<>();

        long allocate() {
            long id = idGen.incrementAndGet();
            cache.put(id, new RecordData());
            return id;
        }

        void setRequestJson(long id, String json) {
            RecordData d = cache.get(id);
            if (d != null && d.requestJson == null) d.requestJson = json;
        }

        void setResponseJson(long id, String json) {
            RecordData d = cache.get(id);
            if (d != null) {
                synchronized (d.responseBuilder) {
                    d.responseBuilder.setLength(0);
                    d.responseBuilder.append(json);
                }
                d.complete = true;
            }
        }

        void appendChunk(long id, byte[] b, int off, int len) {
            RecordData d = cache.get(id);
            if (d != null) {
                synchronized (d.responseBuilder) {
                    d.responseBuilder.append(new String(b, off, len, StandardCharsets.UTF_8));
                }
            }
        }

        void appendChunk(long id, byte b) {
            RecordData d = cache.get(id);
            if (d != null) {
                synchronized (d.responseBuilder) { d.responseBuilder.append((char) b); }
            }
        }

        void markComplete(long id) {
            RecordData d = cache.get(id);
            if (d != null) d.complete = true;
        }

        RecordData consume(long id) { return cache.remove(id); }

        int pendingCount() { return (int) cache.values().stream().filter(d -> !d.complete).count(); }
        int completedCount() { return (int) cache.values().stream().filter(d -> d.complete).count(); }

        void consumeAllCompleted(Consumer<RecordData> handler) {
            cache.entrySet().removeIf(entry -> {
                if (entry.getValue().complete) {
                    try { handler.accept(entry.getValue()); } catch (Exception e) {
                        log.error("消费记录 {} 失败", entry.getKey(), e);
                    }
                    return true;
                }
                return false;
            });
        }

        void cleanupStale(long timeoutMinutes) {
            long threshold = System.currentTimeMillis() - timeoutMinutes * 60_000;
            cache.entrySet().removeIf(entry -> {
                long idTime = entry.getKey() / 10000;
                if (!entry.getValue().complete && idTime < threshold) {
                    log.warn("清理超时流式记录: {}", entry.getKey());
                    return true;
                }
                return false;
            });
        }
    }

    // ────────────── 流式包装 ──────────────

    private static class StreamingResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse delegate;
        private final AppendInputStream body;

        StreamingResponseWrapper(ClientHttpResponse delegate, long recordId, AiRecordCache cache) {
            this.delegate = delegate;
            try { this.body = new AppendInputStream(delegate.getBody(), recordId, cache); }
            catch (IOException e) { throw new RuntimeException("获取流式body失败", e); }
        }

        public HttpStatusCode getStatusCode() throws IOException { return delegate.getStatusCode(); }
        public String getStatusText() throws IOException { return delegate.getStatusText(); }
        public InputStream getBody() { return body; }
        public org.springframework.http.HttpHeaders getHeaders() { return delegate.getHeaders(); }
        public void close() { delegate.close(); }
    }

    private static class AppendInputStream extends InputStream {
        private final InputStream delegate;
        private final long recordId;
        private final AiRecordCache cache;

        AppendInputStream(InputStream delegate, long recordId, AiRecordCache cache) {
            this.delegate = delegate;
            this.recordId = recordId;
            this.cache = cache;
        }

        public int read() throws IOException {
            int b = delegate.read();
            if (b != -1) cache.appendChunk(recordId, (byte) b);
            else cache.markComplete(recordId);
            return b;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int n = delegate.read(b, off, len);
            if (n > 0) cache.appendChunk(recordId, b, off, n);
            else if (n == -1) cache.markComplete(recordId);
            return n;
        }

        public void close() throws IOException { delegate.close(); }
    }

    // ────────────── 非流式包装 ──────────────

    static class CachedBodyClientHttpResponse implements ClientHttpResponse {
        private final ClientHttpResponse delegate;
        private final InputStream cachedBody;

        CachedBodyClientHttpResponse(ClientHttpResponse delegate, byte[] body) {
            this.delegate = delegate;
            this.cachedBody = new ByteArrayInputStream(body);
        }

        public HttpStatusCode getStatusCode() throws IOException { return delegate.getStatusCode(); }
        public String getStatusText() throws IOException { return delegate.getStatusText(); }
        public InputStream getBody() { return cachedBody; }
        public org.springframework.http.HttpHeaders getHeaders() { return delegate.getHeaders(); }
        public void close() { delegate.close(); }
    }
}