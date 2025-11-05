package com.huaji.galgamebyhuaji.model.jwtToken;

import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FrozenUser extends BestEntityToken{
	String email;
	public FrozenUser (){
		super(TokenType.FROZEN_USER);
	}
}
