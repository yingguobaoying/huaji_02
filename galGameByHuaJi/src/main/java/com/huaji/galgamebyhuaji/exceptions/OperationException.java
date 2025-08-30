package com.huaji.galgamebyhuaji.exceptions;

/**
 * 操作失败时抛出此异常作为返回内容
 */
public class OperationException extends RuntimeException {
	public void setMsg (String msg) {
		this.msg = msg;
	}
	
	
	/**
	 * 返回值信息
	 */
	private String msg;
	
	public OperationException (String message) {
		super(message);
		msg = message;
		canIntercept = false;
	}
	public OperationException (String message,boolean canIntercept) {
		super(message);
		msg = message;
		this.canIntercept = canIntercept;
	}
	
	private boolean canIntercept = false;
	
	public boolean isCanIntercept () {
		return canIntercept;
	}
	
	public void setCanIntercept (boolean canIntercept) {
		this.canIntercept = canIntercept;
	}
	
	public String getMsg () {
		return msg;
	}
}