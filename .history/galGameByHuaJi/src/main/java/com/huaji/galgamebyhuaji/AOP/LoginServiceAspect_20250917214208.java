package com.huaji.galgamebyhuaji.AOP;

import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
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
            
            // 特殊处理：login方法使用LoginService.class记录到安全日志
            if ("login".equals(methodName)) {
                MyLogUtil.info(LoginService.class, "安全事件 - " + logMessage + " | 开始时间: " + TimeUtil.);
            } else {
                MyLogUtil.info(LoginServiceAspect.class, logMessage);
            }
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
            
            // 特殊处理：login方法使用LoginService.class记录到安全日志
            if ("login".equals(methodName)) {
                if (success) {
                    MyLogUtil.info(LoginService.class, "安全事件 - " + logMessage);
                } else {
                    MyLogUtil.error(LoginService.class, "安全事件 - " + logMessage);
                }
            } else if ("loginByToken".equals(methodName)) {
                // token登录跳过记录，已在基础认证过滤器处理
                return;
            } else {
                if (success) {
                    MyLogUtil.info(LoginServiceAspect.class, logMessage);
                } else {
                    MyLogUtil.error(LoginServiceAspect.class, logMessage);
                }
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
                        sb.append(maskSensitiveInfo(str));
                    } else {
                        sb.append(args[i].getClass().getSimpleName());
                    }
                } else {
                    sb.append("null");
                }
                
                if (i < args.length - 1) {
                    sb.append(", ");
                }
            }
        }

        return sb.toString();
    }

    /**
     * 构建方法结束日志消息
     */
    private String buildEndLogMessage(String methodName, Object result, boolean success, 
                                    String errorMessage, long executionTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("登录服务方法执行完成: ").append(methodName)
          .append(" | 状态: ").append(success ? "成功" : "失败")
          .append(" | 耗时: ").append(executionTime).append("ms");

        if (!success && StringUtils.hasText(errorMessage)) {
            sb.append(" | 错误信息: ").append(errorMessage);
        }

        if (success && result != null) {
            sb.append(" | 结果: ");
	        switch (result) {
		        case UserToken token -> sb.append("UserToken[userId=").append(token.getUserId())
				        .append(", token=***]");
		        case ReturnResult returnResult ->
				        sb.append("ReturnResult[success=").append(returnResult.isOperationResult())
						        .append(", message=").append(returnResult.getMsg()).append("]");
		        case Users user -> sb.append("Users[userId=").append(user.getUserId())
				        .append(", userName=").append(maskSensitiveInfo(user.getUserName())).append("]");
		        default -> sb.append(result.getClass().getSimpleName());
	        }
        }

        return sb.toString();
    }

    /**
     * 判断参数是否可能包含敏感信息
     */
    private boolean couldBeSensitive(String methodName, int paramIndex) {
        // 根据方法名和参数位置判断是否为敏感信息
        return ("login".equals(methodName) && paramIndex == 0) || // login方法的第一个参数可能是密码
               ("loginByToken".equals(methodName) && paramIndex == 0) || // token
               ("testLogin".equals(methodName) && paramIndex == 0) || // token
               ("register".equals(methodName) && paramIndex == 0); // 注册信息
    }

    /**
     * 敏感信息脱敏处理
     */
    private String maskSensitiveInfo(String info) {
        if (!StringUtils.hasText(info)) {
            return "null";
        }
        
        if (info.length() <= 2) {
            return "***";
        }
        
        // 对可能包含敏感信息的内容进行脱敏
        return info.substring(0, 2) + "***" + (info.length() > 5 ? info.substring(info.length() - 2) : "");
    }
}
