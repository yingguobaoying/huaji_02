package com.huaji.galgamebyhuaji.vignaAiFrame.filter;

public interface MyBaseAdvisor {
	
	/**
	 * 排序生效时,排序越小越前
	 */
	int getIndex ();
	
	/**
	 * 请求发送前的前置方法
	 */
	void beforeAdvise (String sessionId);
	
	/**
	 * 请求完全结束时的回调
	 */
	void afterAdvise (String sessionId);
}