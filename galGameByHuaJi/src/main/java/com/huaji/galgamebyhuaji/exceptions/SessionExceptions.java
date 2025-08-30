package com.huaji.galgamebyhuaji.exceptions;

import com.huaji.galgamebyhuaji.entity.Session;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;

import java.util.List;

public class SessionExceptions extends BestException {
	private static final int ERROR_TYPE = 2;

	public SessionExceptions(String s, int i) {
		super(s, ErrorEnum.getError(ERROR_TYPE, i));
	}

	public SessionExceptions(String s, ErrorEnum errorEnum) {
		super(s, errorEnum);
	}

	public SessionExceptions(String s, List<? extends Session> list, ErrorEnum errorEnum) {
		super(BestException.buildErrorMessage(s, list), errorEnum);
	}
}