package com.huaji.galgamebyhuaji.AOP;

import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 登录服务AOP切面
 * 专门处理登录相关的安全日志记录
 *
 * @author 滑稽/因果报应
 */
@Aspect
@Component
public class LoginServiceAspect {
	
	/**
	 * 切入点：LoginService接口的login方法
	 */
	@Pointcut("execution(* com.huaji.galgamebyhuaji.service.LoginService.login(..))")
	public void loginMethod() {}
	
	/**
	 * 切入点：LoginService接口的所有方法
	 */
	@Pointcut("execution(* com.huaji.galgamebyhuaji.service.LoginService.*(..))")
	public void allLoginServiceMethods() {}
	
	/**
	 * 专门处理login方法的切面
	 */
	@Around("loginMethod()")
	public Object logLoginOperation(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		String startTime = TimeUtil.getVisualDateFormatTime();
		Object[] args = joinPoint.getArgs();
		String loginCredential = extractLoginCredential(args);
		
		// 记录登录开始
		MyLogUtil.info(LoginService.class, "【登录开始】时间: {%s} - 登录凭据: {%s}".formatted(startTime, loginCredential));
		
		Object result = null;
		boolean success = true;
		String errorMsg = null;
		
		try {
			result = joinPoint.proceed();
			return result;
			
		} catch (OperationException e) {
			success = false;
			errorMsg = e.getMsg();
			MyLogUtil.info(LoginService.class, "【登录失败】操作异常: {%s} - 时间: {%s}".formatted(errorMsg, TimeUtil.getVisualDateFormatTime()));
			throw e; // 继续上抛给异常处理器
			
		} catch (Throwable e) {
			success = false;
			errorMsg = e.getMessage();
			MyLogUtil.error(LoginService.class, "【登录异常】系统异常: {%s} - 时间: {%s}".formatted(errorMsg, TimeUtil.getVisualDateFormatTime()));
			MyLogUtil.error(LoginService.class, "【异常详情】", e);
			throw e;
			
		} finally {
			stopWatch.stop();
			long executionTime = stopWatch.getTotalTimeMillis();
			
			if (success && result instanceof UserToken token) {
				MyLogUtil.info(LoginService.class, "【登录成功】用户ID: {%s} - 耗时: {%d}ms - 时间: {%s}".formatted(
						token.getUserId(), executionTime, TimeUtil.getVisualDateFormatTime()));
			}
			else if (!success) {
				MyLogUtil.info(LoginService.class, "【登录结果】失败 - 原因: {%s} - 耗时: {%d}ms - 时间: {%s}".formatted(
						errorMsg, executionTime, TimeUtil.getVisualDateFormatTime()));
			}
		}
	}
	
	/**
	 * 处理LoginService其他方法的切面（排除login方法）
	 */
	@Around("allLoginServiceMethods() && !loginMethod()")
	public Object logOtherLoginOperations(ProceedingJoinPoint joinPoint) throws Throwable {
		String methodName = joinPoint.getSignature().getName();
		// 跳过token登录方法，如您所说在过滤器中处理
		if ("loginByToken".equals(methodName)) {
			return joinPoint.proceed();
		}
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		String startTime = TimeUtil.getVisualDateFormatTime();
		
		MyLogUtil.info(LoginService.class, "【操作开始】方法: {%s} - 时间: {%s}".formatted(methodName, startTime));
		
		Object result = null;
		boolean success = true;
		String errorMsg = null;
		
		try {
			result = joinPoint.proceed();
			return result;
			
		} catch (OperationException e) {
			success = false;
			errorMsg = e.getMsg();
			MyLogUtil.info(LoginService.class, "【操作失败】方法: {%s} - 异常: {%s} - 时间: {%s}".formatted(
					methodName, errorMsg, TimeUtil.getVisualDateFormatTime()));
			throw e;
			
		} catch (Throwable e) {
			success = false;
			errorMsg = e.getMessage();
			MyLogUtil.error(LoginService.class, "【操作异常】方法: {%s} - 异常: {%s} - 时间: {%s}".formatted(
					methodName, errorMsg, TimeUtil.getVisualDateFormatTime()));
			throw e;
			
		} finally {
			stopWatch.stop();
			long executionTime = stopWatch.getTotalTimeMillis();
			
			if (success) {
				MyLogUtil.info(LoginService.class, "【操作成功】方法: {%s} - 耗时: {%s}ms - 时间: {%s}".formatted(
						methodName, executionTime, TimeUtil.getVisualDateFormatTime()));
			}
			else {
				MyLogUtil.info(LoginService.class, "【操作结果】方法: {%s} - 失败 - 原因: {%s} - 耗时: {%s}ms - 时间: {%s}".formatted(
						methodName, errorMsg, executionTime, TimeUtil.getVisualDateFormatTime()));
			}
		}
	}
	
	/**
	 * 从登录参数中提取登录凭据信息（脱敏处理）
	 */
	private String extractLoginCredential(Object[] args) {
		if (args == null || args.length < 1) {
			return "未知凭据";
		}
		
		try {
			// 第一个参数是UsersWithBLOBs
			if (args[0] instanceof UsersWithBLOBs user) {
				HttpServletRequest request = (HttpServletRequest) args[1];
				// 检查使用哪种凭据登录
				return "用户身份信息登录凭证:" + user.getUserNameLogin() + "IP: " + ElseUtil.getClientIp(request);
			}
			return "凭据解析失败";
		} catch (Exception e) {
			MyLogUtil.error(LoginService.class, e);
			return "凭据提取异常";
		}
	}
}
