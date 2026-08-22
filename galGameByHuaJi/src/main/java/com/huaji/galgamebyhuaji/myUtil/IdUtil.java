package com.huaji.galgamebyhuaji.myUtil;

import java.util.UUID;

public class IdUtil {
	public static String getRandomId (String prefix) {
		if ( MyStringUtil.isNull(prefix) )
			prefix = UUID.randomUUID().toString().substring(0, 20);
		if ( prefix.endsWith("_") )
			prefix = prefix.substring(0, prefix.length() - 1);
		String format = String.format("%s_%s_%s", prefix, TimeUtil.getNowTime(), UUID.randomUUID());
		return format.substring(0, 50);
	}
}