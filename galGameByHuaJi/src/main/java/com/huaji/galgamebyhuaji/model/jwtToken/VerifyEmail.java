package com.huaji.galgamebyhuaji.model.jwtToken;

import com.huaji.galgamebyhuaji.enumPackage.TokenType;

/**
 * 虽然这个和另外一个类忘记密码类型的内容完全一致,但是这里为了区分所以单开了一个类
 */
public class VerifyEmail extends BestEntityToken {
	private String email;
	
	public VerifyEmail() {
		super(TokenType.VERIFY_EMAIL);
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
