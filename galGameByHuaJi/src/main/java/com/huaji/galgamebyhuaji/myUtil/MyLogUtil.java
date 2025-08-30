package com.huaji.galgamebyhuaji.myUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 日志工具类
 * 提供统一的日志记录方法
 *
 * @author 滑稽/因果报应
 */
public final class MyLogUtil {
	private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(MyLogUtil.class);

	private MyLogUtil() {
		// 防止实例化
	}

	/**
	 * 记录INFO级别日志
	 *
	 * @param clazz 调用类
	 * @param message 日志信息
	 */
	public static void info(Class<?> clazz, String message) {
		Assert.hasText(message, "Log message must not be empty");
		getLogger(clazz).info(message);
	}

	/**
	 * 记录带参数的INFO级别日志
	 *
	 * @param clazz 调用类
	 * @param format 日志格式字符串
	 * @param arg 参数
	 */
	public static void info(Class<?> clazz, String format, Object arg) {
		Assert.hasText(format, "Log format must not be empty");
		getLogger(clazz).info(format, arg);
	}

	/**
	 * 记录ERROR级别日志
	 *
	 * @param clazz 调用类
	 * @param message 错误信息
	 */
	public static void error(Class<?> clazz, String message) {
		Assert.hasText(message, "Error message must not be empty");
		getLogger(clazz).error(message);
	}

	/**
	 * 记录异常信息
	 *
	 * @param clazz 调用类
	 * @param throwable 异常对象
	 */
	public static void error(Class<?> clazz, Throwable throwable) {
		Assert.notNull(throwable, "Throwable must not be null");
		getLogger(clazz).error(throwable.getMessage(), throwable);
	}

	/**
	 * 记录带上下文的异常信息
	 *
	 * @param clazz 调用类
	 * @param message 错误信息
	 * @param throwable 异常对象
	 */
	public static void error(Class<?> clazz, String message, Throwable throwable) {
		Assert.hasText(message, "Error message must not be empty");
		Assert.notNull(throwable, "Throwable must not be null");
		getLogger(clazz).error(message, throwable);
	}

	/**
	 * 获取日志记录器
	 *
	 * @param clazz 目标类
	 * @return 日志记录器实例
	 */
	private static Logger getLogger(Class<?> clazz) {
		return clazz != null && clazz != Object.class
				? LoggerFactory.getLogger(clazz)
				: DEFAULT_LOGGER;
	}
}