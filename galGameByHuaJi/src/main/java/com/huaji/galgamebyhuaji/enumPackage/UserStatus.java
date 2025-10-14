package com.huaji.galgamebyhuaji.enumPackage;


import lombok.Getter;

@Getter
public enum UserStatus {
	BANNED("banned", "被禁封"), //被封禁
	BLACK_LIST("blacklist", "被拉进黑名单"), //拉黑
	FROZEN("frozen", "被冻结"), //被冻结
	IS_DISABLED("is disabled", "被禁用"), //被禁用
	NOT_AUTHENTICATED("Not authenticated", "未认证邮箱"),//未认证
	OK("ok", "正常") //ok
	;
	
	
	private final String value;
	private final String name;
	
	// 构造方法，只用于设置初始值
	UserStatus(String value, String name) {
		this.value = value;
		this.name = name;
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
