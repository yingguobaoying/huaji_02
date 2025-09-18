package com.huaji.galgamebyhuaji.AOP;

import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 登录服务切面 - 用于记录登录相关操作的日志
 */
@Aspect
@Component
public class LoginServiceAspect {

    /**
     * 定义切点：LoginService接口的所有方法
     */
    @Pointcut("execution(* com.huaji.galgamebyhuaji.service.LoginService.*(..))")
    public void loginServiceMethods() {}

    /**
     * 环绕通知：记录登录服务方法的执行日志
     */
    @Around("loginServiceMethods()")
    public Object logLoginService(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();

        // 记录方法开始执行
        logMethodStart(methodName, args);

        long startTime = System.currentTimeMillis();
        Object result = null;
        boolean success = true;
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            logMethodEnd(methodName, result, success, errorMessage, executionTime);
        }
    }

    /**
     * 记录方法开始执行日志
     */
    private void logMethodStart(String methodName, Object[] args) {
        try {
            String logMessage = buildStartLogMessage(methodName, args);
            MyLogUtil.info(LoginServiceAspect.class, logMessage);
        } catch (Exception e) {
            MyLogUtil.error(LoginServiceAspect.class, "记录方法开始日志失败", e);
        }
    }

    /**
     * 记录方法结束执行日志
     */
    private void logMethodEnd(String methodName, Object result, boolean success, 
                            String errorMessage, long executionTime) {
        try {
            String logMessage = buildEndLogMessage(methodName, result, success, errorMessage, executionTime);
            if (success) {
                MyLogUtil.info(LoginServiceAspect.class, logMessage);
            } else {
                MyLogUtil.error(LoginServiceAspect.class, logMessage);
            }
        } catch (Exception e) {
            MyLogUtil.error(LoginServiceAspect.class, "记录方法结束日志失败", e);
        }
    }

    /**
     * 构建方法开始日志消息
     */
    private String buildStartLogMessage(String methodName, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("开始执行登录服务方法: ").append(methodName);

        if (args != null && args.length > 0) {
            sb.append(" | 参数: ");
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    // 敏感信息处理
                    if (args[i] instanceof UsersWithBLOBs user) {
                        sb.append("UsersWithBLOBs[userId=").append(user.getUserId())
                          .append(", userName=").append(maskSensitiveInfo(user.getUserName()))
                          .append("]");
                    } else if (args[i] instanceof HttpServletRequest) {
                        sb.append("HttpServletRequest");
                    } else if (args[i] instanceof String str && couldBeSensitive(methodName, i)) {
