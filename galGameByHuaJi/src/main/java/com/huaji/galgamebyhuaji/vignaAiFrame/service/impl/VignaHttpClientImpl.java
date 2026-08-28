package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huaji.galgamebyhuaji.constant.AiPromptTemplate;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.ObjectUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.config.VignaHttpClientFactory;
import com.huaji.galgamebyhuaji.vignaAiFrame.filter.MyBaseAdvisor;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatRequiredPara;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.AiMerchantType;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import org.apache.hc.client5.http.async.methods.AbstractCharResponseConsumer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.huaji.galgamebyhuaji.constant.AiParamConstant.*;

/**
 * 使用openai规范的实现类
 */
public class VignaHttpClientImpl extends VignaBaseClient {
    private static final Logger log = LoggerFactory.getLogger(VignaHttpClientImpl.class);
    private static final Logger aiMsgLog = LoggerFactory.getLogger("aiChatAllMsg");
    private final AiClientConfigWithBLOBs config;
    private final List<MyBaseAdvisor> filterList;
    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient httpAsyncClient;
    
    public VignaHttpClientImpl(AiClientConfigWithBLOBs clientConfig, List<MyBaseAdvisor> filterList, boolean sort) {
        this.config = clientConfig;
        if (ListUtil.isNull(filterList))
            this.filterList = List.of();
        else {
            if (sort)
                filterList.sort(Comparator.comparingInt(MyBaseAdvisor::getIndex));
            this.filterList = List.copyOf(filterList);
        }
        httpClient = VignaHttpClientFactory.createHttpClient(clientConfig.getTimeout());
        httpAsyncClient = VignaHttpClientFactory.createAsyncHttpClient(clientConfig.getTimeout());
    }
    
    public VignaHttpClientImpl(AiClientConfigWithBLOBs clientConfig) {
        this(clientConfig, List.of(), false);
    }
    
    @Override
    public String sendAiMsg(ChatRequiredPara para) throws JsonProcessingException {
        // 必要信息检查
        checkRequiredInfo(para);
        final String sessionId = para.getSessionId();
        final ObjectMapper objectMapper = ObjectUtil.getObjectMapper();
        //前导过滤器链
        beforeAdvise(sessionId);
        // 获取经过前导过滤器处理后的最新上下文
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null)
            throw new OperationException("请求上下文不存在，可能已被提前清理");
        //组装请求
        String requestJson = buildRequest(para, context, objectMapper, false);
        //发送请求
        String responseBody = sendRequest(para, context, requestJson);
        JsonNode responseJson = objectMapper.readTree(responseBody);
        String aiContent = responseJson
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();
        // 当前 stream=false，正常情况下不会进入这里。
        // 保留该逻辑以兼容部分模型返回的特殊结构。
        if (MyStringUtil.isNull(aiContent)) aiContent = responseJson
                .path("choices")
                .path(0)
                .path("delta")
                .path("content")
                .asText();
        context.setFinishReason(responseBody);
        VignaMsg aiMsg = new VignaMsg();
        aiMsg.setRole(VignaRole.ai);
        aiMsg.setContent(aiContent);
        context.setAiReply(aiMsg);
        // 响应解析完成后重新写入上下文
        ChatContextMap.setContext(sessionId, context);
        //调试输出:解析结果
        log.debug("普通请求解析完成, sessionId: {}, 回复内容长度: {}, 内容: {}",
                  sessionId, aiContent.length(), aiContent);
        //后导过滤器链
        afterAdvise(sessionId);
        //本次请求生命周期结束
        return aiContent;
    }
    
    private void checkRequiredInfo(ChatRequiredPara para) {
        if (!hasKey)
            throw new OperationException("请求上下文失败,因为密钥服务未注册");
        if (para == null)
            throw new OperationException("请求参数不能为空");
        AiClientConfigWithBLOBs config = ObjectUtil.mergeObject(this.config, para.getConfig());
        String sessionId = para.getSessionId();
        if (MyStringUtil.isNull(sessionId))
            throw new OperationException("SessionId不能为空");
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null)
            throw new OperationException("请求上下文不存在，可能已被提前清理");
        String apiKey = keyServlet.getApiKey(config);
        if (MyStringUtil.isNull(apiKey))
            throw new OperationException("错误!APIkey读取失败!");
    }
    
    private void beforeAdvise(String sessionId) {
        for (MyBaseAdvisor advisor : filterList) {
            advisor.beforeAdvise(sessionId);
        }
    }
    
    /**
     * 组装 AI 请求
     * <p>
     * 负责：
     * 1. 获取当前消息
     * 2. 获取系统消息
     * 3. 整理历史消息
     * 4. 构造 messages
     * 5. 写入模型参数
     * 6. 合并额外 JSON 配置
     * 7. 将最终请求 JSON 写入 Context
     * <p>
     * 不负责发送 HTTP 请求。
     */
    private String buildRequest(ChatRequiredPara para, VignaMsgContext context, ObjectMapper objectMapper, boolean stream) throws JsonProcessingException {
        AiClientConfigWithBLOBs config = ObjectUtil.mergeObject(this.config, para.getConfig());
        final String sessionId = para.getSessionId();
        // 获取当前用户消息
        VignaMsg userMsg;
        if (para.isSumUp()) {
            userMsg = new VignaMsg();
            userMsg.setRole(VignaRole.user);
            userMsg.setContent(AiPromptTemplate.CHAT_SUMMARY_PROMPT);
            context.setContent(userMsg);
        } else {
            userMsg = context.getContent();
        }
        // 获取系统消息，没有则使用客户端默认配置
        VignaMsg systemMsg = context.getSystemMsg();
        if (systemMsg == null) {
            systemMsg = new VignaMsg();
            systemMsg.setRole(VignaRole.system);
            systemMsg.setContent(config.getContent());
            context.setSystemMsg(systemMsg);
        }
        // 这里只负责按照 index 排序
        List<VignaMsg> historyMsgList =
                context.getHistoryMsgList();
        if (ListUtil.isNull(historyMsgList))
            historyMsgList = List.of();
        else
            historyMsgList.sort(Comparator.comparingInt(VignaMsg::getIndex));
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode messages = root.putArray("messages");
        // 第一条为系统消息
        messages.add(systemMsg.getJson());
        // 历史消息
        for (VignaMsg msg : historyMsgList) messages.add(msg.getJson());
        //不检查最后一条消息是不是 user。
        if (userMsg == null) {
            if (!para.isSumUp())
                log.warn("警告!当前用户消息为空且不为总结模式!可能导致回复错误!历史消息如下:{}", root);
        } else {
            messages.add(userMsg.getJson());
        }
        // 基础模型参数
        root.put("model", config.getModel());
        root.put("temperature",
                 config.getTemperature() != null ? config.getTemperature() / 100.0 : DEFAULT_TEMPERATURE);
        root.put("top_p", config.getTopP() != null ? config.getTopP() / 100.0 : DEFAULT_TOP_P);
        root.put("frequency_penalty",
                 config.getFrequencyPenalty() != null ? config.getFrequencyPenalty() / 100.0 : DEFAULT_FREQ_PENALTY);
        root.put("presence_penalty",
                 config.getPresencePenalty() != null ? config.getPresencePenalty() / 100.0 : DEFAULT_PRES_PENALTY);
        root.put("max_token", config.getMaxTokens() != null ? config.getMaxTokens() : MAX_TOKEN);
        // 是否流式请求,由调用方决定(普通请求 false,流式请求 true)
        root.put("stream", stream);
        // 合并额外 JSON 配置
        String extraConfigJson =
                config.getExtraConfigJson();
        if (StringUtils.hasText(extraConfigJson)) {
            try {
                JsonNode extraNode = objectMapper.readTree(extraConfigJson);
                if (extraNode.isObject())
                    extraNode.properties().forEach(entry ->
                                                           root.set(entry.getKey(), entry.getValue()));
            } catch (JsonProcessingException e) {
                log.error(
                        "解析额外请求JSON失败, sessionId: {}, error: {}",
                        sessionId, e.getMessage(), e);
                // 普通请求失败后清除 Context。
                if (!para.isSumUp()) {
                    ChatContextMap.delContext(sessionId);
                }
                throw e;
            }
        }
        // 最终请求 JSON
        String requestJson = root.toString();
        log.info("即将开始请求:{},原始参数:{}", sessionId, requestJson);
        // 写入请求上下文
        context.setSendJson(requestJson);
        context.setTrySize(0);
        ChatContextMap.setContext(sessionId, context);
        return requestJson;
    }
    
    /**
     * 发送 HTTP 请求
     * <p>
     * 这里只负责：
     * 1. 获取 API Key
     * 2. 构造 HTTP 请求
     * 3. 执行 HTTP 请求
     * 4. 判断 HTTP 状态码
     * 5. 处理可重试异常
     * 6. 获取完整响应体
     * <p>
     * 不解析 AI 响应内容。
     * <p>
     */
    private String sendRequest(ChatRequiredPara para, VignaMsgContext context, String requestJson) {
        AiClientConfigWithBLOBs config = ObjectUtil.mergeObject(this.config, para.getConfig());
        final String sessionId = para.getSessionId();
        String apiKey = keyServlet.getApiKey(config);
        if (MyStringUtil.isNull(apiKey))
            throw new OperationException("错误!APIkey读取失败!");
        int maxTrySize = config.getMaxTrySize() == null ? 3 : config.getMaxTrySize();
        for (int attempt = 1; attempt <= maxTrySize; attempt++) {
            // 重试退避
            if (attempt > 1) {
                long waitMillis = ElseUtil.getNextTime(attempt - 1);
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (!para.isSumUp()) {
                        ChatContextMap.delContext(sessionId);
                    }
                    throw new OperationException("请求被中断: " + e.getMessage());
                }
            }
            // 更新本次尝试信息
            context.setTrySize(attempt);
            context.setSendTime(new Date());
            context.setOutTime(config.getTimeout());
            // 更新上下文
            ChatContextMap.setContext(sessionId, context);
            try {
                log.info("执行请求, sessionId: {}, 尝试次数: {}/{}",
                         sessionId, attempt, maxTrySize);
                // 记录发送信息到 AI 全量收发日志(API Key 脱敏)
                aiMsgLog.info("【发送请求】sessionId={}, 目标URL={}, APIKey={}, 请求体={}",
                              sessionId, config.getBaseUrl(), maskApiKey(apiKey), requestJson);
                // 构造 HTTP 请求
                ClassicHttpRequest httpRequest =
                        ClassicRequestBuilder
                                .post()
                                .setUri(config.getBaseUrl())
                                .setHeader("Authorization", "Bearer " + apiKey)
                                .setHeader("Content-Type", "application/json")
                                .setEntity(new StringEntity(requestJson, ContentType.parse("UTF-8")))
                                .build();
                
                return httpClient.execute(
                        httpRequest, response -> {
                            int statusCode = response.getCode();
                            HttpEntity entity = response.getEntity();
                            String body = entity != null ? EntityUtils.toString(
                                    entity, StandardCharsets.UTF_8) : "";
                            // 记录接收信息到 AI 全量收发日志
                            aiMsgLog.info("【接收响应】sessionId={}, 状态码={}, 响应体={}",
                                          sessionId, statusCode, body);
                            // HTTP 请求失败
                            if (statusCode < 200 || statusCode >= 300) {
                                log.error("请求体->{}\n路径->{}\n请求失败信息->{}",
                                          requestJson, config.getBaseUrl(), body);
                                String errorMsg = String.format("HTTP %d: %s", statusCode, body);
                                //500+/429是临时性错误，可以重试。
                                if (statusCode >= 500 || statusCode == 429)
                                    throw new IOException(errorMsg);
                                // 其他 4xx：
                                throw new IllegalArgumentException(errorMsg);
                            }
                            log.info("请求成功, sessionId: {}, 状态码: {}, 响应体长度: {}",
                                     sessionId, statusCode, body.length());
                            return body;
                        });
            } catch (IllegalArgumentException e) {
                log.error("请求失败且不可重试, sessionId: {}, 尝试次数: {}/{}, 错误: {}",
                          sessionId, attempt, maxTrySize, e.getMessage());
                if (!para.isSumUp()) ChatContextMap.delContext(sessionId);
                throw new OperationException("请求参数错误，停止重试: " + e.getMessage());
            } catch (Exception e) {
                log.warn("请求失败, sessionId: {}, 尝试次数: {}/{}, 错误: {}",
                         sessionId, attempt, maxTrySize, e.getMessage());
            }
        }
        // 所有重试均失败
        log.error("AI模型调用最终失败, sessionId: {}, 已尝试 {} 次",
                  sessionId, maxTrySize);
        if (!para.isSumUp()) ChatContextMap.delContext(sessionId);
        throw new OperationException("AI模型调用失败，已重试 " + maxTrySize + " 次");
    }
    
    private void afterAdvise(String sessionId) {
        for (MyBaseAdvisor advisor : filterList) {
            try {
                advisor.afterAdvise(sessionId);
            } catch (Exception e) {
                log.error("错误过滤器执行过程中出现错误:{},排序为:{}的过滤器调用跳过",
                          e.getMessage(), advisor.getIndex(), e);
            }
        }
        ChatContextMap.delContext(sessionId);
        
    }
    
    @Override
    public Flux<String> sendAiMsgByStream(ChatRequiredPara para) {
        AiClientConfigWithBLOBs config = ObjectUtil.mergeObject(this.config, para.getConfig());
        final String sessionId = para.getSessionId();
        final boolean isSumUp = para.isSumUp();
        //桥接 sink:multicast 支持多订阅者(本地记录 + 外部消费)
        final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer(1024, false);
        
        //本地订阅:累积完整内容,并在流式请求彻底完成后触发后置过滤器链
        final StringBuilder aiContent = new StringBuilder();
        sink.asFlux()
                .doOnNext(aiContent::append)
                .doFinally(signal -> {
                    if (signal == SignalType.ON_COMPLETE) {
                        //流式请求彻底完成:写回上下文并触发后置过滤器链(记录AI回复)
                        finishStreamContext(sessionId, aiContent.toString());
                    } else {
                        //出错或被取消:清理上下文(总结请求除外)
                        if (!isSumUp) ChatContextMap.delContext(sessionId);
                        log.error("流式请求出错!已经记录的内容如下{}", aiContent);
                    }
                })
                .subscribe();
        
        try {
            //必要信息检查
            checkRequiredInfo(para);
            final ObjectMapper objectMapper = ObjectUtil.getObjectMapper();
            //前导过滤器链
            beforeAdvise(sessionId);
            //获取上下文,这里能触发自动总结
            VignaMsgContext context = ChatContextMap.getContext(sessionId);
            if (context == null) {
                sink.tryEmitError(new OperationException("请求上下文不存在，可能已被提前清理"));
                return sink.asFlux();
            }
            String requestJson = buildRequest(para, context, objectMapper, true);
            //获取 API Key
            String apiKey = keyServlet.getApiKey(config);
            if (MyStringUtil.isNull(apiKey)) {
                if (!isSumUp) ChatContextMap.delContext(sessionId);//终止性错误进行处理
                sink.tryEmitError(new OperationException("错误!APIkey读取失败!"));
                return sink.asFlux();
            }
            //构造异步 HTTP 请求(SSE)
            SimpleHttpRequest request = SimpleRequestBuilder.post(config.getBaseUrl())
                    .setHeader("Authorization", "Bearer " + apiKey)
                    .setHeader("Content-Type", "application/json")
                    .setHeader("Accept", "text/event-stream")
                    .setBody(requestJson, ContentType.APPLICATION_JSON)
                    .build();
            //更新上下文
            context.setTrySize(1);
            context.setSendTime(new Date());
            context.setSum(false);//流式请求禁用总结模式
            context.setOutTime(config.getTimeout());
            ChatContextMap.setContext(sessionId, context);
            log.info("即将开始流式请求:{},原始参数:{}", sessionId, requestJson);
            // 记录发送信息到 AI 全量收发日志(API Key 脱敏)
            aiMsgLog.info("【发送流式请求】sessionId={}, 目标URL={}, APIKey={}, 请求体={}",
                          sessionId, config.getBaseUrl(), maskApiKey(apiKey), requestJson);
            
            //执行异步请求,把 SSE 增量桥接到 sink
            httpAsyncClient.execute(
                    SimpleRequestProducer.create(request),
                    new AbstractCharResponseConsumer<Void>() {
                        private final StringBuilder lineBuffer = new StringBuilder();
                        
                        @Override
                        public void releaseResources() {
                            //ResourceHolder 接口抽象方法,流式场景无需额外释放资源
                        }
                        
                        @Override
                        protected void start(HttpResponse response, ContentType contentType) throws IOException {
                            int code = response.getCode();
                            if (code < 200 || code >= 300) {
                                throw new IOException(String.format("流式请求失败, HTTP %d", code));
                            }
                        }
                        
                        @Override
                        protected int capacityIncrement() {
                            return Integer.MAX_VALUE;
                        }
                        
                        @Override
                        protected void data(CharBuffer src, boolean endOfStream) {
                            while (src.hasRemaining()) {
                                char c = src.get();
                                if (c == '\n') {
                                    handleSseLine(lineBuffer.toString().trim(), sink);
                                    lineBuffer.setLength(0);
                                } else if (c != '\r') {
                                    lineBuffer.append(c);
                                }
                            }
                            if (endOfStream) {
                                //处理缓冲区可能残留的最后一行
                                String lastLine = lineBuffer.toString().trim();
                                if (!lastLine.isEmpty()) {
                                    handleSseLine(lastLine, sink);
                                }
                                sink.tryEmitComplete();
                            }
                        }
                        
                        @Override
                        protected Void buildResult() {
                            return null;
                        }
                        
                        @Override
                        public void failed(Exception cause) {
                            sink.tryEmitError(cause);
                        }
                    },
                    null);
        } catch (Exception e) {
            log.error("流式请求启动失败, sessionId: {}, 错误: {}", sessionId, e.getMessage(), e);
            if (!isSumUp) ChatContextMap.delContext(sessionId);
            sink.tryEmitError(e);
        }
        return sink.asFlux();
    }
    
    
    /**
     * 解析单行 SSE 数据,提取 content 增量并发射到 sink
     */
    private void handleSseLine(String line, Sinks.Many<String> sink) {
        if (line.isEmpty()) return;
        //SSE 注释行,忽略
        if (line.startsWith(":")) return;
        if (!line.startsWith("data:")) return;
        String data = line.substring("data:".length()).trim();
        //流结束标记
        if (data.equals("[DONE]")) return;
        try {
            JsonNode node = ObjectUtil.getObjectMapper().readTree(data);
            JsonNode deltaContent = node.path("choices").path(0).path("delta").path("content");
            if (deltaContent.isTextual()) {
                String text = deltaContent.asText();
                if (!text.isEmpty()) {
                    sink.tryEmitNext(text);
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("解析流式响应块失败, 原始数据: {}", data, e);
        }
    }
    
    /**
     * 流式请求彻底完成后的收尾:把完整内容写回上下文并触发后置过滤器链
     */
    private void finishStreamContext(String sessionId, String aiContent) {
        try {
            VignaMsgContext context = ChatContextMap.getContext(sessionId);
            if (context == null) {
                log.warn("流式请求收尾时上下文不存在, sessionId: {}", sessionId);
                return;
            }
            //流式响应没有完整 JSON,这里把拼接后的完整内容记录下来
            context.setFinishReason(aiContent);
            VignaMsg aiMsg = new VignaMsg();
            aiMsg.setRole(VignaRole.ai);
            aiMsg.setContent(aiContent);
            context.setAiReply(aiMsg);
            ChatContextMap.setContext(sessionId, context);
            // 记录接收信息到 AI 全量收发日志
            aiMsgLog.info("【接收流式响应】sessionId={}, 响应内容={}", sessionId, aiContent);
            afterAdvise(sessionId);
        } catch (Exception e) {
            log.error("流式请求收尾处理失败, sessionId: {}, 错误: {}", sessionId, e.getMessage(), e);
        }
    }
    
    /**
     * API Key 脱敏:仅保留前4位和后4位,中间以 **** 代替
     * <p>过短密钥(长度<=8)仅保留首尾各1位,避免泄露</p>
     */
    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) return apiKey;
        int len = apiKey.length();
        if (len <= 8) {
            if (len <= 2) return "****";
            return apiKey.charAt(0) + "****" + apiKey.charAt(len - 1);
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(len - 4);
    }
    
    @Override
    public AiMerchantType getType() {
        return AiMerchantType.OPEN_AI;
    }
}
