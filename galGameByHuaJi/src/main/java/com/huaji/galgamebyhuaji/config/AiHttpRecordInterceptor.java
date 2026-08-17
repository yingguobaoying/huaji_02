package com.huaji.galgamebyhuaji.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 层拦截器 —— 只记录 AI API 的原始请求/响应 JSON 到 aiChatAllMsg 日志。
 * 不进行任何数据库操作、缓存、或状态管理。
 */
@Slf4j
public class AiHttpRecordInterceptor implements ClientHttpRequestInterceptor {

    private static final org.slf4j.Logger aiMsgLog = LoggerFactory.getLogger("aiChatAllMsg");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        // —— 记录 HTTP 请求体 ——
        if (body != null && body.length > 0) {
            String contentType = request.getHeaders().getContentType() != null
                    ? request.getHeaders().getContentType().toString() : "";
            if (contentType.contains("json")) {
                aiMsgLog.info("[HTTP请求] {} {}\n{}", request.getMethod(), request.getURI(),
                        new String(body, StandardCharsets.UTF_8));
            }
        }

        // —— 执行真正的 HTTP 请求 ——
        ClientHttpResponse response = execution.execute(request, body);

        // —— 判断流式/非流式 ——
        String respContentType = response.getHeaders().getContentType() != null
                ? response.getHeaders().getContentType().toString() : "";
        boolean isStream = respContentType.contains("text/event-stream") || respContentType.contains("stream");

        if (isStream) {
            // 流式响应的内容由 AiRecordAdvisor / aiChatByStream.doOnComplete 处理
            return response;
        } else {
            // 非流式：记录完整响应体
            byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
            aiMsgLog.info("[HTTP响应] {}\n{}", response.getStatusCode(),
                    new String(responseBody, StandardCharsets.UTF_8));
            return new CachedBodyClientHttpResponse(response, responseBody);
        }
    }

    // ────────────── 响应体缓存包装 (使 body 可多次读取) ──────────────

    private static class CachedBodyClientHttpResponse implements ClientHttpResponse {
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