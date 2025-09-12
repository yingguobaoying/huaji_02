package com.huaji.galgamebyhuaji.model.jwtToken;

import com.huaji.galgamebyhuaji.enumPackage.TokenType;

public class LostPasswordUser extends OnlineUser {
	private String email;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public LostPasswordUser() {
		super(TokenType.LOST_PASSWORD);
	}
}
