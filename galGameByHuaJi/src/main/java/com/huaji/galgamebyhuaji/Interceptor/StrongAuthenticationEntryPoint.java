package com.huaji.galgamebyhuaji.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import io.lettuce.core.dynamic.batch.BatchException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StrongAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
	                     AuthenticationException authException) throws IOException {
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.OK.value());
		
		ReturnResult<Exception> byd = ReturnResult.isFalse("");
		// 根据异常类型提供更具体的错误信息
		if (authException instanceof InsufficientAuthenticationException) {
			byd.setMxg("您的权限不足,无法进行此操作");
		} else if (authException instanceof AuthenticationServiceException) {
			byd.setMxg("Token无效或验证失败,请重新登录后重试");
		} else {
			byd.setMxg(authException.getMessage());
		}
		// 使用Jackson序列化
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getOutputStream(), byd);
		// 记录日志
		MyLogUtil.error(StrongAuthenticationEntryPoint.class,
		                "强认证失败: 路径={%s}, 原因={%s}, IP={%s}".formatted(
				                request.getServletPath(), authException.getMessage(), ElseUtil.getClientIp(request)));
	}
	
	public void isFalse(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		ReturnResult<Exception> byd = ReturnResult.isFalse("");
		if (ex instanceof OperationException o) {
			byd.setMxg(o.getMsg());
		} else if (ex instanceof BatchException e) {
			byd.setMxg(e.getMessage());
			MyLogUtil.error(StrongAuthenticationEntryPoint.class, e);
		} else {
			byd.setMxg("出错了!请您重新登录后再试一次");
			MyLogUtil.error(StrongAuthenticationEntryPoint.class, ex);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getOutputStream(), byd);
	}
}
