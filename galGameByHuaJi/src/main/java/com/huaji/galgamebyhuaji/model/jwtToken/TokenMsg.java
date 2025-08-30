package com.huaji.galgamebyhuaji.model.jwtToken;

import java.util.Date;

public class TokenMsg {
	public TokenMsg (Date expiration, String token) {
		this.expiration = expiration;
		this.token = token;
	}
	
	public TokenMsg () {
	}
	
	private String token;
	private Date expiration;
	
	public String getToken () {
		return token;
	}
	
	public void setToken (String token) {
		this.token = token;
	}
	
	public Date getExpiration () {
		return expiration;
	}
	
	public void setExpiration (Date expiration) {
		this.expiration = expiration;
	}
}