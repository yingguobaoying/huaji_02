package com.huaji.galgamebyhuaji.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TokenMsg {
	public TokenMsg(Date expiration, String token) {
		this.expiration = expiration;
		this.token = token;
	}
	
	public TokenMsg() {
	}
	
	private String token;
	private Date expiration;
	
}
