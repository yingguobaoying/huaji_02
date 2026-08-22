package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.fasterxml.jackson.core.JacksonException;
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
	private final CloseableHttpClient httpClient;
	
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
		if ( !hasKey )
			throw new OperationException("请求上下文失败,因为密钥服务未注册");
		final String sessionId = para.getSessionId();
		ObjectMapper objectMapper = ObjectUtil.getObjectMapper();
		//前置 Advisor
		for ( MyBaseAdvisor myBaseAdvisor : filterList ) myBaseAdvisor.beforeAdvise(sessionId);
		//		 获取本次请求上下文
		VignaMsgContext context = ChatContextMap.getContext(sessionId);
		if ( context == null ) throw new OperationException("请求上下文不存在，可能已被提前清理");
		VignaMsg userMsg;
		if ( para.isSumUp() ) {
			userMsg = new VignaMsg();
			userMsg.setRole(VignaRole.user);
			userMsg.setContent(AiPromptTemplate.CHAT_SUMMARY_PROMPT);
			context.setContent(userMsg);
		} else userMsg = context.getContent();
		//获取系统消息，没有则使用客户端默认配置
		VignaMsg systemMsg = context.getSystemMsg();
		if ( systemMsg == null ) {
			systemMsg = new VignaMsg();
			systemMsg.setRole(VignaRole.system);
			systemMsg.setContent(config.getContent());
			context.setSystemMsg(systemMsg);
		}
		//获取并整理历史消息这里只负责按照 index 排序。
		List<VignaMsg> historyMsgList = context.getHistoryMsgList();
		if ( ListUtil.isNull(historyMsgList) )
			historyMsgList = List.of();
		else
			historyMsgList.sort(Comparator.comparingInt(VignaMsg::getIndex));
		//构造请求 JSON
		ObjectNode root = objectMapper.createObjectNode();
		ArrayNode messages = root.putArray("messages");
		// 第一条为系统消息
		messages.add(systemMsg.getJson());
		// 历史消息
		for ( VignaMsg msg : historyMsgList ) messages.add(msg.getJson());
		//不检查最后一条消息是不是 user,因为总结模式下当前消息本身就是 system
		if ( userMsg == null ) {
			if ( !para.isSumUp() ) {
				log.warn("警告!当前用户消息为空且不为总结模式!可能导致回复错误!历史消息如下:{}",
						root);
			}
		} else {
			messages.add(userMsg.getJson());
		}
		//基础模型参数
		root.put("model", config.getModel());
		root.put(
				"temperature",
				config.getTemperature() != null
						? config.getTemperature()
						: DEFAULT_TEMPERATURE);
		root.put(
				"top_p",
				config.getTopP() != null
						? config.getTopP()
						: DEFAULT_TOP_P);
		root.put(
				"frequency_penalty",
				config.getFrequencyPenalty() != null
						? config.getFrequencyPenalty()
						: DEFAULT_FREQ_PENALTY);
		root.put(
				"presence_penalty",
				config.getPresencePenalty() != null
						? config.getPresencePenalty()
						: DEFAULT_PRES_PENALTY);
		root.put("stream", false);
		//合并额外 JSON 配置如果配置不对则本次请求直接失败，
		String extraConfigJson = config.getExtraConfigJson();
		if ( StringUtils.hasText(extraConfigJson) ) {
			try {
				JsonNode extraNode = objectMapper.readTree(extraConfigJson);
				if ( extraNode.isObject() ) {
					extraNode.properties().forEach(entry -> {
						root.set(entry.getKey(), entry.getValue());
					});
				}
			} catch ( JsonProcessingException e ) {
				log.error(
						"解析额外请求JSON失败, sessionId: {}, error: {}",
						sessionId, e.getMessage(), e);
				ChatContextMap.consumptionContext(sessionId);
				throw e;
			}
		}
		String requestJson = root.toString();
		log.info("即将开始请求:{},原始参数:{}", sessionId, requestJson);
		context.setSendJson(requestJson);
		context.setTrySize(0);
		int maxTrySize = config.getMaxTrySize() == null
				? 3 : config.getMaxTrySize();
		String responseBody = null;
		boolean requestSuccess = false;
		String apiKey = keyServlet.getApiKey(config);
		if ( MyStringUtil.isNull(apiKey) )
			throw new OperationException("错误!APIkey读取失败!");
		for ( int attempt = 1; attempt <= maxTrySize; attempt++ ) {
			if ( attempt > 1 ) {
				long waitMillis = ElseUtil.getNextTime(attempt - 1);
				try {
					Thread.sleep(waitMillis);
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
					ChatContextMap.consumptionContext(sessionId);
					throw new OperationException("请求被中断: " + e.getMessage());
				}
			}
			context.setTrySize(attempt);
			context.setSendTime(new Date());
			context.setOutTime(config.getTimeout());
			//更新上下文
			ChatContextMap.setContext(sessionId, context);
			try {
				log.info("执行请求, sessionId: {}, 尝试次数: {}/{}", sessionId, attempt, maxTrySize);
				//构造 HTTP 请求
				ClassicHttpRequest httpRequest = ClassicRequestBuilder
						.post()
						.setUri(config.getBaseUrl())
						.setHeader("Authorization", "Bearer " + apiKey)
						.setHeader("Content-Type", "application/json")
						.setEntity(new StringEntity(requestJson, ContentType.parse("UTF-8")))
						.build();
				//执行 HTTP 请求 只负责获取响应,判断 HTTP 状态码,解析 AI 回复,写入 context
				responseBody = httpClient.execute(httpRequest, response -> {
					int statusCode = response.getCode();
					HttpEntity entity = response.getEntity();
					String body = entity != null
							? EntityUtils.toString(entity, StandardCharsets.UTF_8)
							: "";
					if ( statusCode < 200 || statusCode >= 300 ) {
						log.error("请求体->{}\n路径->{}\n请求失败信息->{}", requestJson, config.getBaseUrl(), body);
						String errorMsg = String.format("HTTP %d: %s", statusCode, body);
						//500或者429认为是临时问题进行重试
						if ( statusCode >= 500 || statusCode == 429 ) throw new IOException(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					}
					log.info("请求成功, sessionId: {}, 状态码: {}, 响应体长度: {}",
							sessionId, statusCode, body.length());
					JsonNode responseJson = objectMapper.readTree(body);
					String aiContent = responseJson
							.path("choices")
							.path(0)
							.path("message")
							.path("content")
							.asText();
					//当前 stream=false，正常情况下应该不会进入这里。但以防万一
					if ( MyStringUtil.isNull(aiContent) ) {
						aiContent = responseJson
								.path("choices")
								.path(0)
								.path("delta")
								.path("content")
								.asText();
					}
					context.setFinishReason(body);
					VignaMsg aiMsg = new VignaMsg();
					aiMsg.setRole(VignaRole.ai);
					aiMsg.setContent(aiContent);
					context.setAiReply(aiMsg);
					ChatContextMap.setContext(sessionId, context);
					return body;
				});
				requestSuccess = true;
				break;
			} catch ( IllegalArgumentException | JacksonException e ) {
				log.error("请求失败且不可重试, sessionId: {}, 尝试次数: {}/{}, 错误: {}",
						sessionId, attempt, maxTrySize, e.getMessage());
				ChatContextMap.consumptionContext(sessionId);
				throw new OperationException("请求参数或响应格式错误，停止重试: " + e.getMessage());
			} catch ( Exception e ) {
				log.warn(
						"请求失败, sessionId: {}, 尝试次数: {}/{}, 错误: {}",
						sessionId, attempt, maxTrySize, e.getMessage());
			}
		}
		if ( !requestSuccess ) {
			log.error(
					"AI模型调用最终失败, sessionId: {}, 已尝试 {} 次",
					sessionId, maxTrySize);
			//全部请求完成上下文失去意义ban了
			ChatContextMap.consumptionContext(sessionId);
			throw new OperationException("AI模型调用失败，已重试 " + maxTrySize + " 次");
		}
		for ( MyBaseAdvisor advisor : filterList ) {
			try {
				advisor.afterAdvise(sessionId);
			} catch ( Exception e ) {
				log.error("错误过滤器执行过程中出现错误:{},排序为:{}的过滤器调用跳过",
						e.getMessage(), advisor.getIndex(), e);
			}
		}
		//本次请求生命周期结束
		ChatContextMap.consumptionContext(sessionId);
		return responseBody;
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