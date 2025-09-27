package com.huaji.galgamebyhuaji.myUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author 滑稽/因果报应
 */
public class TimeUtil {
	public static String getSimpleDateFormatTime(Date date) {
		if (date == null)
			date = new Date();
		return new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(date);
	}
	
	public static String getVisualDateFormatTime(Date date) {
		if (date == null)
			date = new Date();
		return new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(date);
	}
	
	public static String getVisualDateFormatTime() {
		return getVisualDateFormatTime(null);
	}
	
	public static String getNowTime() {
		return new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss").format(new Date());
	}
	
	public static boolean isYesterdayOrEarlier(Date date) {
		if (date == null) {
			return false;
		}
		LocalDate inputDate = date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		LocalDate today = LocalDate.now(); // 默认使用系统时区
		return inputDate.isBefore(today);
	}
	
	public static Date getFutureTime(int hour, int minute, int second) {
		return new Date(
				System.currentTimeMillis() + hour * 60L * 60 * 1000 + minute * 60L * 1000 + second * 1000L
		);
	}
	
	public static Date getFutureTimeByHour(int hour) {
		return getFutureTime(hour, 0, 0);
	}
}
