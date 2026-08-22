package com.huaji.galgamebyhuaji.vignaAiFrame.message;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 包含系统请求时的完整内容,以及请求上下文窗口内容
 */
@Getter
@Setter
public class VignaMsgContext {
	/**
	 * 当前消息内容
	 */
	private VignaMsg content;
	/**
	 * ai响应内容
	 */
	private VignaMsg aiReply;
	/**
	 * 历史记录消息(不包含当前消息)
	 */
	private List<VignaMsg> historyMsgList = new ArrayList<>();;
	/**
	 * 系统消息
	 */
	private VignaMsg systemMsg;
	/**
	 * 完成结果(原始json)
	 */
	private String finishReason;
	/**
	 * 发送的json
	 */
	private String sendJson;
	/**
	 * 属于用户
	 */
	private int userId;
	/**
	 * 用户会话标识
	 */
	private String sessionId;
	/**
	 * 创建时间
	 */
	private Date creation = new Date();
	/**
	 * 上次请求发送时间
	 */
	private Date sendTime = null;
	/**
	 * 请求重试次数
	 */
	private int trySize = 0;
	/**
	 * 请求超时时间(毫秒)
	 */
	private long outTime;
	private boolean isSum = false;
	/**
	 * 使用的客户端配置ID
	 */
	private long clientId;
	public boolean isDel () {
		if ( sendTime == null ) return false;
		return System.currentTimeMillis() > sendTime.getTime() + outTime;
	}
	
	public VignaMsgContext () {
	}
	
	@Override
	public boolean equals (Object o) {
		if ( this == o ) return true;
		if ( !(o instanceof VignaMsgContext that) ) return false;
		return userId == that.userId && Objects.equals(sessionId, that.sessionId);
	}
	
	@Override
	public int hashCode () {
		return Objects.hash(userId, sessionId);
	}
}