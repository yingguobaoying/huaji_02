package com.huaji.galgamebyhuaji.vignaAiFrame.service;

import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import reactor.core.publisher.Flux;

/**
 * ai聊天接口
 */
public interface VignaAiChat {
	/**
	 * 进行ai对话
	 *
	 * @param msg       消息内容,为总结消息时这个参数不生效
	 * @param sessionId 会话id(用于获取对话上下文)
	 * @param userId    用户ID
	 * @param clientId  客户端ID
	 * @param isSumUp   是否为对话总结
	 */
	ReturnResult<String> vignaAiChat (VignaMsg msg, String sessionId, int userId, long clientId, boolean isSumUp);
	
	Flux<String> vignaAiChatByStream (VignaMsg msg, String sessionId, int userId, long clientId);
}