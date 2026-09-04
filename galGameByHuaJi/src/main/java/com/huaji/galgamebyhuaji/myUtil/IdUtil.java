package com.huaji.galgamebyhuaji.myUtil;

import java.util.UUID;

public class IdUtil {
	public static String getRandomId(String prefix) {
		if (MyStringUtil.isNull(prefix)) {
			prefix = UUID.randomUUID().toString().substring(0, 20);
		}
		// 去除末尾所有下划线
		prefix = prefix.replaceAll("_+$", "");
		String format = String.format("%s%s_%s", prefix, TimeUtil.getNowTime(), UUID.randomUUID());
		return format.substring(0, 50);
	}
}
