package com.huaji.galgamebyhuaji.vignaAiFrame.myenum;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum VignaRole {
	user("用户输入", "user", 1),
	system("系统消息", "system", 2),
	ai("模型返回", "assistant", 3),
	tool("工具调用结果", "tool", 4),
	sum("ai总结内容", "user", 5),
	;
	private final String name;//这玩意无任何作用仅用于日志记录并不会影响最后json
	private final String value;
	private final int code;
	
	VignaRole (String name, String value, int code) {
		this.name = name;
		this.value = value;
		this.code = code;
	}
	
	private static final Map<Integer, VignaRole> CODE_MAP = new HashMap<>();
	
	static {
		for ( VignaRole v : VignaRole.values() ) {
			CODE_MAP.put(v.code, v);
		}
	}
	
	public static VignaRole getType (Integer role) {
		if ( role == null ) return user;
		return CODE_MAP.getOrDefault(role, user);
	}
}