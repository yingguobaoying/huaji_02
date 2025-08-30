package com.huaji.galgamebyhuaji.exceptions;

/**
 * @author 滑稽/因果报应
 *
 */
public class WriteError extends BestRuntimeException {
	protected Integer errorType = 3;
	/**
	 * 附加信息
	 */
	private String mxg;

	public void setMxg(String mxg) {
		this.mxg = mxg;
	}

	public String getMxg() {
		return mxg;
	}

	public WriteError(String mxg, int errorNum, int expect, int actual) {
		super(mxg + "预期更新/插入数据个数为:" + expect + "实际个数为:" + actual, errorNum);
		this.mxg = mxg;
		this.errorNum = errorNum;
	}

	public WriteError(String mxg, int errorNum, int actual) {
		this(mxg, errorNum, 1, actual);
	}

	public WriteError(int errorNum, int actual) {
		super((errorNum == 1 ? "数据库读写错误!预期更新/插入数据个数为:1,实际个数为:" + actual : ("数据库错误,错误代码为:" + errorNum)));
	}

	/**
	 * 尝试写入多个
	 * 此方法推荐使用模式:tryWrite(xxxMapper.insert(xxx),xxx);
	 * @param actual 实际写入个数,推荐直接写插入/更新语句(返回值为影响记录数量)
	 * @param expect 预期个数
	 */
	public static void tryWrite(int actual, int expect) {
		if (actual != expect)
			throw new WriteError("预期更新/插入/删除数据个数为:" + expect + "实际个数为:" + actual, 1, expect, actual);
	}

	/**
	 * 尝试写入一个
	 * 此方法推荐使用模式:tryWrite(xxxMapper.insert(xxx));
	 * @param actual 实际写入个数,推荐直接写插入/更新语句(返回值为影响记录数量)
	 */
	public static void tryWrite(int actual) {
		if (actual != 1)
			throw new WriteError("预期更新/插入/删除数据个数为:1,实际个数为:" + actual, 1, actual);
	}

}