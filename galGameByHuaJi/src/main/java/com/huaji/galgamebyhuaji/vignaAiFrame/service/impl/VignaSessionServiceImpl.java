package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class VignaSessionServiceImpl implements VignaSessionService {
	private final AiChatMsgService chatMsgService;
	
	// 使用 ConcurrentHashMap 维护会话状态
	// TRUE = 空闲, FALSE = 占用
	private final ConcurrentHashMap<String, Boolean> sessionContext = new ConcurrentHashMap<>(50);
	
	@Override
	public int sessionLeisure (String sessionId) {
		boolean hasSession = chatMsgService.hasSession(sessionId);
		if ( !hasSession ) return 0;
		
		Boolean status = sessionContext.get(sessionId);
		// null 或 TRUE 都视为空闲
		return Boolean.FALSE.equals(status) ? 2 : 1;
	}
	@Override
	public String getSessionId (int userId, long clientId) {
		return "";
	}
	
	@Override
	public Set<String> getUserSessionId (int userId) {
		return Set.of();
	}
	
	@Override
	public void lockSession (String sessionId) {
		// 只有当 key 不存在，或者存在但值为 TRUE 时，才能成功放入 FALSE
		Boolean previous = sessionContext.putIfAbsent(sessionId, Boolean.FALSE);
		// 如果之前已经存在且为 FALSE，说明被占用
		if ( Boolean.FALSE.equals(previous) ) throw new OperationException("调用失败，此会话正在处理中，请勿重复提交");
	}
	
	@Override
	public void unlockSession (String sessionId) {
		sessionContext.remove(sessionId, Boolean.FALSE);
	}
}