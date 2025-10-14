package com.huaji.galgamebyhuaji.enumPackage;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.jwtToken.BuyResourcesUser;
import com.huaji.galgamebyhuaji.model.jwtToken.LostPasswordUser;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.model.jwtToken.VerifyEmail;
import lombok.Getter;

@Getter
public enum TokenType {
	/**
	 * 此状态为默认值,为临时登录时使用
	 */
	DEFAULT_STATUS(0, "保持登录", OnlineUser.class),
	GET_DOWNLOAD(2, "下载", BuyResourcesUser.class),
	LOST_PASSWORD(1, "密码丢失", LostPasswordUser.class),
	VERIFY_EMAIL(3, "验证邮箱", VerifyEmail.class);
	
	
	private final String statusName;
	private final Class tokenClazz;
	
	TokenType(Integer statusNum, String statusName, Class tokenClazz) {
		this.statusName = statusName;
		this.tokenClazz = tokenClazz;
		this.statusNum = statusNum;
	}
	
	private final Integer statusNum;
	
	
	public int getStatusNum() {
		return statusNum;
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
