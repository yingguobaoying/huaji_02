package com.huaji.galgamebyhuaji.vignaAiFrame.filter;//package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiRecordAdvisor implements MyBaseAdvisor {
	private final AiChatMsgService chatMsgService;
	
	/**
	 * 排序生效时,排序越小越前
	 */
	@Override
	public int getIndex () {
		return 2;
	}
	
	/**
	 * 请求发送前的前置方法
	 *
	 * @param sessionId
	 */
	@Override
	@Transactional
	public void beforeAdvise (String sessionId) {//进行记录用户消息
		AiRecordWithBLOBs r = new AiRecordWithBLOBs();
		r.setSessionId(sessionId);
		VignaMsgContext context = ChatContextMap.getContext(sessionId);
		if ( context == null ) throw new OperationException("请求上下文不存在，可能已被提前清理");
		VignaMsg userMsg = context.getContent();
		r.setContent(userMsg.getContent());
		r.setRole(userMsg.getRole().getCode());
		r.setChatIndex(userMsg.getIndex());
		r.setClientId(context.getClientId());
		r.setCreatedAt(new Date());
		r.setUserId(context.getUserId());
		chatMsgService.installData(r);
	}
	
	/**
	 * 请求完全结束时的回调
	 *
	 * @param sessionId
	 */
	@Override
	@Transactional
	public void afterAdvise (String sessionId) {
		AiRecordWithBLOBs r = new AiRecordWithBLOBs();
		r.setSessionId(sessionId);
		VignaMsgContext context = ChatContextMap.getContext(sessionId);
		if ( context == null ) throw new OperationException("请求上下文不存在，可能已被提前清理");
		r.setRequestJson(context.getFinishReason());
		VignaMsg aiReply = context.getAiReply();
		if ( aiReply == null ) {
			log.error("错误!AI接口返回内容为空!已跳过本次ai回复记录!");
			return;
		}
		r.setContent(aiReply.getContent());
		r.setChatIndex(context.getContent().getIndex() + 1);
		r.setCreatedAt(new Date());
		r.setUserId(context.getUserId());
		r.setClientId(context.getClientId());
		r.setPromptContent(context.getSystemMsg().getContent());
		r.setSessionId(sessionId);
		r.setRole(aiReply.getRole().getCode());
		//token信息暂时不记录
		chatMsgService.installData(r);
	}
	
}