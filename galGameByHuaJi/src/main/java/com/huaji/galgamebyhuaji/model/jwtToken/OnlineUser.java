package com.huaji.galgamebyhuaji.model.jwtToken;

import com.huaji.galgamebyhuaji.enumPackage.TokenType;

import java.util.Objects;

public class OnlineUser {
	private int userId;
	private TokenType tokenType;
	private String ip;
	
	
	public String getIp () {
		return ip;
	}
	
	public void setIp (String ip) {
		this.ip = ip;
	}
	
	public int getUserId () {
		return userId;
	}
	
	public void setUserId (int userId) {
		this.userId = userId;
	}
	
	public TokenType getTokenType () {
		return tokenType;
	}
	
	public void setTokenType (TokenType tokenType) {
		this.tokenType = tokenType;
	}
	
	public OnlineUser () {
	}
	
	public OnlineUser (TokenType tokenType) {
		this.tokenType = tokenType;
	}
	
	@Override
	public boolean equals (Object o) {
		if ( this == o ) return true;
		if ( !(o instanceof OnlineUser that) ) return false;
		return userId == that.userId && tokenType == that.tokenType && Objects.equals(ip, that.ip);
	}
	
	@Override
	public int hashCode () {
		return Objects.hash(userId, tokenType, ip);
	}
}