package com.huaji.galgamebyhuaji.enumPackage;

import com.huaji.galgamebyhuaji.exceptions.OperationException;

public enum TokenType {
	/**
	 * 此状态为默认值,为临时登录时使用
	 */
	DEFAULT_STATUS(0, "保持登录"),
	LOST_PASSWORD(1, "密码丢失"),
	GET_DOWNLOAD(2, "下载"),
	;
	private final String statusName;

	private TokenType(Integer statusNum, String statusName) {
		this.statusName = statusName;
		this.statusNum = statusNum;
	}

	private final Integer statusNum;


	public int getStatusNum() {
		return statusNum;
	}

	public String getStatusName() {
		return statusName;
	}

	public static TokenType getTokenType(Integer statusNum) {
		return switch (statusNum) {
			case 0 -> DEFAULT_STATUS;
			case 1 -> LOST_PASSWORD;
			case null -> DEFAULT_STATUS;
			default -> throw new OperationException("不支持的类型: " + statusNum);
		};
	}
}