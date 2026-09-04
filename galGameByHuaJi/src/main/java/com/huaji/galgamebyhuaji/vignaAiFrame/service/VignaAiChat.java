package com.huaji.galgamebyhuaji.vignaAiFrame.service;

import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatServicePara;
import reactor.core.publisher.Flux;

/**
 * ai聊天接口
 */
public interface VignaAiChat {
	/**
	 * 进行ai对话
	 */
	ReturnResult<String> vignaAiChat (ChatServicePara para);
	Flux<String> vignaAiChatByStream (ChatServicePara para);
}
