package com.huaji.galgamebyhuaji.exceptions;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;

import java.util.List;

public class UserException extends BestException {
	private static final int ERROR_TYPE = 1;
	
	public UserException(String s, List<? extends Users> list, ErrorEnum errorEnum) {
		super(BestException.buildErrorMessage(s, list), errorEnum);
		setErrorType(ERROR_TYPE);
	}
}
