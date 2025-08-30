package com.huaji.galgamebyhuaji.enumPackage;

public enum LoginStatus {
	ONLINE("online"),
	OFFLINE("offline"),
	NOT_AVAILABLE("not available");
	private String value;

	private LoginStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}


	public static LoginStatus getLoginStatus(String value) {
		for (LoginStatus loginStatus : LoginStatus.values()) {
			if (loginStatus.value.equals(value)) {
				return loginStatus;
			}
		}
		throw new RuntimeException("类型错误!在修改登录状态时使用了未定义或已经弃用的状态信息");
	}

}