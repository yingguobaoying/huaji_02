package com.huaji.galgamebyhuaji.vignaAiFrame;

import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ai上下文仓库
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatContextMap {
	/**
	 * 请求上下文
	 */
	private static ConcurrentHashMap<String, VignaMsgContext> contextMap = new ConcurrentHashMap(50);
	
	/**
	 * 清理过期的上下文
	 */
	public static void DisposalOfCorpse () {
		Enumeration<String> keys = contextMap.keys();
		while ( keys.hasMoreElements() ) {
			String key = keys.nextElement();
			if ( !contextMap.containsKey(key) ) contextMap.remove(key);
			VignaMsgContext context = contextMap.get(key);
			if ( context == null ) {
				contextMap.remove(key);
				continue;
			}
			if ( context.isDel() ) contextMap.remove(key);
		}
	}
	
	/**
	 * 设置/更新上下文
	 */
	public static void setContext (String sessionId, VignaMsgContext msgContext) {
		contextMap.put(sessionId, msgContext);
	}
	
	/**
	 * 仅获取不消费
	 */
	public static VignaMsgContext getContext (String sessionId) {
		return contextMap.get(sessionId);
	}
	
	/**
	 * 本次请求生命周期结束，无论成功失败，都销毁 Context
	 */
	public static VignaMsgContext consumptionContext (String sessionId) {
		VignaMsgContext vignaMsgContext = contextMap.get(sessionId);
		contextMap.remove(sessionId);
		return vignaMsgContext;
	}
}