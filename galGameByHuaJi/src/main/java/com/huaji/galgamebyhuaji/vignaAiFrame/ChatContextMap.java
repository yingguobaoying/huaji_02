package com.huaji.galgamebyhuaji.vignaAiFrame;

import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ai上下文仓库
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatContextMap {
	/**
	 * 请求上下文仓库
	 */
	private static final ConcurrentHashMap<String, VignaMsgContext> contextMap = new ConcurrentHashMap<>(100);
	
	/**
	 * 清理过期的/已标记删除的上下文
	 */
	public static void DisposalOfCorpse () {
		// 使用 entrySet 迭代是 ConcurrentHashMap 推荐的线程安全遍历方式
		for ( Map.Entry<String, VignaMsgContext> entry : contextMap.entrySet() ) {
			String key = entry.getKey();
			VignaMsgContext context = entry.getValue();
			// 如果上下文为 null，或者被标记为删除，则移除
			// 使用 remove(key, value) 确保只删除当前版本的 Context，防止误删新写入的 Context
			if ( context == null || context.isDel() ) {
				contextMap.remove(key, context);
			}
		}
	}
	
	/**
	 * 设置/更新上下文
	 */
	public static void setContext (String sessionId, VignaMsgContext msgContext) {
		if ( sessionId == null || msgContext == null ) {
			log.warn("尝试设置无效的上下文, sessionId: {}", sessionId);
			return;
		}
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
	 * 使用原子操作，避免“先 get 再 remove”导致的并发覆盖问题
	 */
	public static VignaMsgContext delContext (String sessionId) {
		return contextMap.remove(sessionId);
	}
}