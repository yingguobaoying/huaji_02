package com.huaji.galgamebyhuaji.myUtil;

import java.util.UUID;

public class IdUtil {
	public static String getRandomId (String prefix) {
		String format = String.format("%s_%s_%s", prefix, TimeUtil.getNowTime(), UUID.randomUUID());
		return format.substring(0, 40);
	}
}