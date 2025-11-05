package com.huaji.galgamebyhuaji.model.jwtToken;

import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnfrozenUser extends BestEntityToken{
	String newPassword;//解冻需要重置密码
	String email;
	public UnfrozenUser(){
		super(TokenType.UNFROZEN_USER);
	}
}
