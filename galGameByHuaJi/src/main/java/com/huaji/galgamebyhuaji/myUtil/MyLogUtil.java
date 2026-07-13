package com.huaji.galgamebyhuaji.myUtil;

import com.huaji.galgamebyhuaji.entity.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 日志工具类 提供统一的日志记录方法
 * 由于此工具是在未引入lombok时进行日志记录时使用的,
 * 而现在引入了lombok因此废弃了该类.
 * 请之后请使用{@link  lombok.extern.slf4j.Slf4j}注解代替
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
     * @param clazz   调用类
     * @param message 日志信息
     */
    @Deprecated(since = "1.0")
    public static void info(Class<?> clazz, String message) {
        Assert.hasText(message, "Log message must not be empty");
        getLogger(clazz).info(message);
    }
    
    
    /**
     * 记录ERROR级别日志
     *
     * @param clazz   调用类
     * @param message 错误信息
     */
    @Deprecated(since = "1.0")
    public static void error(Class<?> clazz, String message) {
        Assert.hasText(message, "Error message must not be empty");
        getLogger(clazz).error(message);
    }
    
    /**
     * 记录异常信息
     *
     * @param clazz     调用类
     * @param throwable 异常对象
     */
    @Deprecated(since = "1.0")
    public static void error(Class<?> clazz, Throwable throwable) {
        Assert.notNull(throwable, "Throwable must not be null");
        getLogger(clazz).error(throwable.getMessage(), throwable);
    }
    
    /**
     * 记录带上下文的异常信息
     *
     * @param clazz     调用类
     * @param message   错误信息
     * @param throwable 异常对象
     */
    @Deprecated(since = "1.0")
    public static void error(Class<?> clazz, String message, Throwable throwable) {
        Assert.hasText(message, "Error message must not be empty");
        Assert.notNull(throwable, "Throwable must not be null");
        getLogger(clazz).error(message, throwable);
    }
    
    /**
     * 获取日志记录器
     *
     * @param clazz 目标类
     *
     * @return 日志记录器实例
     */
    @Deprecated(since = "1.0")
    private static Logger getLogger(Class<?> clazz) {
        return clazz != null && clazz != Object.class
                ? LoggerFactory.getLogger(clazz)
                : DEFAULT_LOGGER;
    }
    
    @Deprecated(since = "1.0")
    public static void UserBehaviorLog(Class<?> clazz, String s, Users u) {
        info(clazz, "用户%d:{%s}进行了%s".formatted(u.getUserId(), u.getUserName(), s));
    }
    
    public static void UserBehaviorLog(Logger log, String s, Users u) {
        if (log != null)
            log.info("用户{}:{}进行了{}", u.getUserId(), u.getUserName(), s);
        else
            DEFAULT_LOGGER.info("用户{}:{}进行了{}", u.getUserId(), u.getUserName(), s);
        
    }
}
