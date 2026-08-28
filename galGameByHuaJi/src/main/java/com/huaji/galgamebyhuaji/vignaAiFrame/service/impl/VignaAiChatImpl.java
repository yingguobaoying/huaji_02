package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.config.VignaChatClientConfig;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatRequiredPara;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class VignaAiChatImpl implements VignaAiChat {
	
	@Override
	public ReturnResult<String> vignaAiChat (VignaMsg msg, String sessionId, int userId, long clientId, boolean isSumUp) {
		VignaMsgContext context;
		if ( isSumUp ) {
			//如果为总结调用那么会话上下文存在
			context = ChatContextMap.getContext(sessionId);
			if ( context == null )
				return ReturnResult.isFalse("总结失败,因为上下文已被清除");
		} else {
			//普通情况下需要建立上下文
			context = new VignaMsgContext();
			context.setSessionId(sessionId);
			context.setClientId(clientId);
			context.setUserId(userId);
			context.setContent(msg);
			context.setSum(false);
			ChatContextMap.setContext(sessionId, context);
		}
		VignaHttpClient chatClient = VignaChatClientConfig.getChatClient(clientId);
		ChatRequiredPara chatRequiredPara = new ChatRequiredPara();
		chatRequiredPara.setSessionId(sessionId);
		chatRequiredPara.setSumUp(isSumUp);
		try {
			String s = chatClient.sendAiMsg(chatRequiredPara);
			return ReturnResult.isTrue("ai请求成功", s);
		} catch ( Exception e ) {
			if(e instanceof OperationException o)
				return ReturnResult.isError("系统出现错误:"+e.getMessage()+"已经终止了请求!");
			return ReturnResult.isFalse("AI请求出错:" + e.getMessage());
		}finally {
			if(isSumUp)
				ChatContextMap.delContext(sessionId);
		}
	}
	
	@Override
	public Flux<String> vignaAiChatByStream (VignaMsg msg, String sessionId, int userId, long clientId) {
		//流式请求与普通请求一样需要先建立上下文
		VignaMsgContext context = new VignaMsgContext();
		context.setSessionId(sessionId);
		context.setClientId(clientId);
		context.setUserId(userId);
		context.setContent(msg);
		context.setSum(false);
		ChatContextMap.setContext(sessionId, context);
		VignaHttpClient chatClient = VignaChatClientConfig.getChatClient(clientId);
		if ( chatClient == null )
			return Flux.error(new OperationException("AI客户端不存在, clientId: " + clientId));
		ChatRequiredPara chatRequiredPara = new ChatRequiredPara();
		chatRequiredPara.setSessionId(sessionId);
		chatRequiredPara.setSumUp(false);
		return chatClient.sendAiMsgByStream(chatRequiredPara);
	}
}
