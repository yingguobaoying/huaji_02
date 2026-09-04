package com.huaji.galgamebyhuaji.vignaAiFrame.message;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huaji.galgamebyhuaji.myUtil.ObjectUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * AI消息类
 */
@Getter
@Setter
public class VignaMsg {
	/**
	 * 本消息角色身份
	 */
	private VignaRole role;
	/**
	 * 本条消息内容
	 */
	private String content;
	/**
	 * 属于对话下标
	 */
	private int index;
	/**
	 * 可选，tool 调用时使用,这两玩意暂时不打算支持先撂在这里
	 */
	private String name;
	private List<VignaTool> tools;
	
	public VignaMsg(VignaRole role, String content, int index) {
		this.role = role;
		this.content = content;
		this.index = index;
	}
	
	public VignaMsg() {
	}
	
	
	public ObjectNode getJson () {
		ObjectNode root = ObjectUtil.getObjectMapper().createObjectNode();
		root.put("role", role.getValue());
		root.put("content", content);
		return root;
	}
}
