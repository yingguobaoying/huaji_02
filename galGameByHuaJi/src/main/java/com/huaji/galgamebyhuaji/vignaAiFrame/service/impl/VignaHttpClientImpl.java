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
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
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
	private final AiClientConfigWithBLOBs config;
	private final List<MyBaseAdvisor> filterList;
	private CloseableHttpClient httpClient;
	
	public VignaHttpClientImpl (AiClientConfigWithBLOBs clientConfig, List<MyBaseAdvisor> filterList, boolean sort) {
		this.config = clientConfig;
		if ( ListUtil.isNull(filterList) )
			this.filterList = List.of();
		else {
			if ( sort )
				filterList.sort(Comparator.comparingInt(MyBaseAdvisor::getIndex));
			this.filterList = List.copyOf(filterList);
		}
		httpClient = VignaHttpClientFactory.createHttpClient(clientConfig.getTimeout());
	}
	
	public VignaHttpClientImpl (AiClientConfigWithBLOBs clientConfig) {
		this(clientConfig, List.of(), false);
	}
	
	@Override
	public String sendAiMsg (ChatRequiredPara para) throws JsonProcessingException {
		// 必要信息检查
		checkRequiredInfo(para);
		final String sessionId = para.getSessionId();
		final ObjectMapper objectMapper = ObjectUtil.getObjectMapper();
		//前导过滤器链
		beforeAdvise(sessionId);
		// 获取经过前导过滤器处理后的最新上下文
		VignaMsgContext context = ChatContextMap.getContext(sessionId);
		if ( context == null )
			throw new OperationException("请求上下文不存在，可能已被提前清理");
		//组装请求
		String requestJson = buildRequest(para, context, objectMapper);
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
		if ( MyStringUtil.isNull(aiContent) ) aiContent = responseJson
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
		//后导过滤器链
		afterAdvise(sessionId);
		//本次请求生命周期结束
		return aiContent;
	}
	
	private void checkRequiredInfo (ChatRequiredPara para) {
		if ( !hasKey )
			throw new OperationException("请求上下文失败,因为密钥服务未注册");
		if ( para == null )
			throw new OperationException("请求参数不能为空");
		String sessionId = para.getSessionId();
		if ( MyStringUtil.isNull(sessionId) )
			throw new OperationException("SessionId不能为空");
		VignaMsgContext context = ChatContextMap.getContext(sessionId);
		if ( context == null )
			throw new OperationException("请求上下文不存在，可能已被提前清理");
		String apiKey = keyServlet.getApiKey(config);
		if ( MyStringUtil.isNull(apiKey) )
			throw new OperationException("错误!APIkey读取失败!");
	}
	
	private void beforeAdvise (String sessionId) {
		for ( MyBaseAdvisor advisor : filterList ) {
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
	private String buildRequest (ChatRequiredPara para, VignaMsgContext context, ObjectMapper objectMapper) throws JsonProcessingException {
		final String sessionId = para.getSessionId();
		// 获取当前用户消息
		VignaMsg userMsg;
		if ( para.isSumUp() ) {
			userMsg = new VignaMsg();
			userMsg.setRole(VignaRole.user);
			userMsg.setContent(AiPromptTemplate.CHAT_SUMMARY_PROMPT);
			context.setContent(userMsg);
		} else {
			userMsg = context.getContent();
		}
		// 获取系统消息，没有则使用客户端默认配置
		VignaMsg systemMsg = context.getSystemMsg();
		if ( systemMsg == null ) {
			systemMsg = new VignaMsg();
			systemMsg.setRole(VignaRole.system);
			systemMsg.setContent(config.getContent());
			context.setSystemMsg(systemMsg);
		}
		// 这里只负责按照 index 排序
		List<VignaMsg> historyMsgList =
				context.getHistoryMsgList();
		if ( ListUtil.isNull(historyMsgList) )
			historyMsgList = List.of();
		else
			historyMsgList.sort(Comparator.comparingInt(VignaMsg::getIndex));
		ObjectNode root = objectMapper.createObjectNode();
		ArrayNode messages = root.putArray("messages");
		// 第一条为系统消息
		messages.add(systemMsg.getJson());
		// 历史消息
		for ( VignaMsg msg : historyMsgList ) messages.add(msg.getJson());
		//不检查最后一条消息是不是 user。
		if ( userMsg == null ) {
			if ( !para.isSumUp() )
				log.warn("警告!当前用户消息为空且不为总结模式!可能导致回复错误!历史消息如下:{}", root);
		} else {
			messages.add(userMsg.getJson());
		}
		// 基础模型参数
		root.put("model", config.getModel());
		root.put("temperature", config.getTemperature() != null ? config.getTemperature() : DEFAULT_TEMPERATURE);
		root.put("top_p", config.getTopP() != null ? config.getTopP() : DEFAULT_TOP_P);
		root.put("frequency_penalty",
				config.getFrequencyPenalty() != null ? config.getFrequencyPenalty() : DEFAULT_FREQ_PENALTY);
		root.put("presence_penalty",
				config.getPresencePenalty() != null ? config.getPresencePenalty() : DEFAULT_PRES_PENALTY);
		// 当前普通请求
		root.put("stream", false);
		// 合并额外 JSON 配置
		String extraConfigJson =
				config.getExtraConfigJson();
		if ( StringUtils.hasText(extraConfigJson) ) {
			try {
				JsonNode extraNode = objectMapper.readTree(extraConfigJson);
				if ( extraNode.isObject() )
					extraNode.properties().forEach(entry ->
							root.set(entry.getKey(), entry.getValue()));
			} catch ( JsonProcessingException e ) {
				log.error(
						"解析额外请求JSON失败, sessionId: {}, error: {}",
						sessionId, e.getMessage(), e);
				// 普通请求失败后清除 Context。
				if ( !para.isSumUp() ) {
					ChatContextMap.delContext(sessionId);
				}
				throw e;
			}
		}
		// 最终请求 JSON
		String requestJson = root.toString();
		log.info(
				"即将开始请求:{},原始参数:{}",
				sessionId, requestJson);
		// 写入请求上下文
		context.setSendJson(requestJson);
		context.setTrySize(0);
		ChatContextMap.setContext(
				sessionId, context);
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
	private String sendRequest (ChatRequiredPara para, VignaMsgContext context, String requestJson) {
		final String sessionId = para.getSessionId();
		String apiKey = keyServlet.getApiKey(config);
		if ( MyStringUtil.isNull(apiKey) )
			throw new OperationException("错误!APIkey读取失败!");
		int maxTrySize = config.getMaxTrySize() == null ? 3 : config.getMaxTrySize();
		for ( int attempt = 1; attempt <= maxTrySize; attempt++ ) {
			// 重试退避
			if ( attempt > 1 ) {
				long waitMillis = ElseUtil.getNextTime(attempt - 1);
				try {
					Thread.sleep(waitMillis);
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
					if ( !para.isSumUp() ) {
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
							// HTTP 请求失败
							if ( statusCode < 200 || statusCode >= 300 ) {
								log.error("请求体->{}\n路径->{}\n请求失败信息->{}",
										requestJson, config.getBaseUrl(), body);
								String errorMsg = String.format("HTTP %d: %s", statusCode, body);
								//500+/429是临时性错误，可以重试。
								if ( statusCode >= 500 || statusCode == 429 )
									throw new IOException(errorMsg);
								// 其他 4xx：
								throw new IllegalArgumentException(errorMsg);
							}
							log.info(
									"请求成功, sessionId: {}, 状态码: {}, 响应体长度: {}",
									sessionId, statusCode, body.length());
							return body;
						});
			} catch ( IllegalArgumentException e ) {
				log.error("请求失败且不可重试, sessionId: {}, 尝试次数: {}/{}, 错误: {}",
						sessionId, attempt, maxTrySize, e.getMessage());
				if ( !para.isSumUp() ) ChatContextMap.delContext(sessionId);
				throw new OperationException("请求参数错误，停止重试: " + e.getMessage());
			} catch ( Exception e ) {
				log.warn("请求失败, sessionId: {}, 尝试次数: {}/{}, 错误: {}",
						sessionId, attempt, maxTrySize, e.getMessage());
			}
		}
		// 所有重试均失败
		log.error("AI模型调用最终失败, sessionId: {}, 已尝试 {} 次",
				sessionId, maxTrySize);
		if ( !para.isSumUp() ) {
			ChatContextMap.delContext(sessionId);
		}
		throw new OperationException("AI模型调用失败，已重试 " + maxTrySize + " 次");
	}
	
	private void afterAdvise (String sessionId) {
		for ( MyBaseAdvisor advisor : filterList ) {
			try {
				advisor.afterAdvise(sessionId);
			} catch ( Exception e ) {
				log.error("错误过滤器执行过程中出现错误:{},排序为:{}的过滤器调用跳过",
						e.getMessage(), advisor.getIndex(), e);
			}
		}
	}
	
	@Override
	public Flux<String> sendAiMsgByStream (ChatRequiredPara para) {
		return null;
	}
	
	@Override
	public AiMerchantType getType () {
		return AiMerchantType.OPEN_AI;
	}
}