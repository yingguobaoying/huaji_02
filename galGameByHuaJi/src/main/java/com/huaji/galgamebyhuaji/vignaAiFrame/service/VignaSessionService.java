package com.huaji.galgamebyhuaji.vignaAiFrame.service;

import java.util.Set;

public interface VignaSessionService {
	/**
	 * 检查对应session是否空闲
	 * @return 0:不存在的会话
	 * 1: 空闲
	 * 2: 占用
	 */
	int sessionLeisure (String sessionId);
	
	/**
	 * 获取一个全新的会话,并检查用户是否有权限使用
	 */
	String getSessionId (int userId,long clientId);
	/**
	 * 获取用户的历史会话记录ID
	 */
	Set<String> getUserSessionId (int userId);
	void lockSession(String sessionId);
	void unlockSession(String sessionId);
    
    void testSessionUser(String sessionId, int user);
}
