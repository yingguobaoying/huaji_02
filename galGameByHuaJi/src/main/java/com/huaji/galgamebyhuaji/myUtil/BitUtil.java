package com.huaji.galgamebyhuaji.myUtil;

public class BitUtil {
	/**
	 * 用来转化bit为bool类型的工具
	 * @param bit 需要转化的比特
	 * @return 1=true else false
	 */
	public static boolean isTrue(Byte bit) {
		return bit != null && bit == 1;
	}
}