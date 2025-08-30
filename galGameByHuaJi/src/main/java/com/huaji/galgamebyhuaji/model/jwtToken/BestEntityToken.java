package com.huaji.galgamebyhuaji.model.jwtToken;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;

@JsonIgnoreProperties({"userId", "tokenType", "ip"})
public class BestEntityToken extends OnlineUser {
	
	@Override
	public void setTokenType (TokenType tokenType) {}
	
	public BestEntityToken () {
	}
	
	protected BestEntityToken (TokenType tokenType) {
		super(tokenType);
	}
}