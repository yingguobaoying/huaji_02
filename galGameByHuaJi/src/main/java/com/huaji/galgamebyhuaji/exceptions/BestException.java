package com.huaji.galgamebyhuaji.exceptions;


import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;

import java.util.List;

/**
 * 基础异常类型,包括了一个自带的错误代码,自带了set and get 方法
 * 错误代码0表示未设置错误
 * 错误类别0表示未设置
 * 错误类别1表示java自带的错误
 * @author 滑稽/因果报应
 *
 */
public class BestException extends Exception {
	/**
	 * 错误代码
	 */
	private Integer errorNum;
	/**
	 * 错误类别(继承此类的子类应重新设置)
	 * 比如
	 * protected Integer errorType = 1;表示用户异常
	 */
	private Integer errorType = 0;

	protected BestException(String s, ErrorEnum error) {
		super(s + "," + error.getError_msg());
		this.errorNum = error.getError_num();
		this.errorType = error.getError_type();
	}

	public Integer getErrorType() {
		return errorType;
	}

	protected void setErrorType(Integer errorType) {
		this.errorType = errorType;
	}


	public BestException(String msg) {
		super(msg);
		this.errorNum = 0;
		this.errorType = 0;
	}

	public Integer getErrorNum() {
		return errorNum;
	}

	protected void setErrorNum(Integer errorNum) {
		this.errorNum = errorNum;
	}

	//用于构建异常的详细信息
	public static <T> String buildErrorMessage(String message, List<T> objList) {
		StringBuilder sb = new StringBuilder(message);
		sb.append(": 出现异常的对象列表如下\n");
		if (objList == null || objList.isEmpty()) {
			sb.append("对象列表为空!\n");
		} else
			for (T r : objList) {
				sb.append(r.toString())
						.append("\n");
			}
		return sb.toString();
	}
}