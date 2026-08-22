package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class VignaAiChatImpl implements VignaAiChat {
	@Override
	public ReturnResult<String> vignaAiChat (VignaMsg msg, String sessionId, boolean isSumUp) {
		
		return null;
	}
	
	@Override
	public Flux<String> vignaAiChatByStream (VignaMsg msg, String sessionId) {
		return null;
	}
}