package com.huaji.galgamebyhuaji.vignaAiFrame.model;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ChatRequiredPara {
	/**
	 * 会话ID
	 */
	private String sessionId;
	/**
	 * 此消息是否为总结信息
	 */
	private boolean isSumUp = false;
	/**
	 * 覆盖的默认配置
	 */
	private AiClientConfigWithBLOBs config;
	/**
	 * 额外的json参数
	 */
	private Map<String, Object> extraJson;
}