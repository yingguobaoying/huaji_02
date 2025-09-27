package com.huaji.galgamebyhuaji.enumPackage;


public enum UserStatus {
	OK("ok", "正常"),//ok
	BLACK_LIST("blacklist", "在黑名单中"),//拉黑
	FROZEN("frozen", "用户冻结"),//被冻结
	BANNED("banned", "用户被禁封"),//被封禁
	IS_DISABLED("is disabled", "用户被禁用"),//被禁用
	NOT_AUTHENTICATED("Not authenticated", "未认证邮箱");//未认证
	
	
	public String getName() {
		return name;
	}
	
	
	private final String value;
	private final String name;
	
	// 构造方法，只用于设置初始值
	UserStatus(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public static UserStatus testEnumValue(String value) {
		for (UserStatus userStatus : UserStatus.values()) {
			if (userStatus.getValue().equalsIgnoreCase(value)) {
				return userStatus;
			}
		}
		throw new RuntimeException("用户状态错误: 在修改用户状态时使用了未定义或已经弃用的状态信息");
	}
	
}
