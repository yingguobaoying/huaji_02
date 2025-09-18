package com.huaji.galgamebyhuaji.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class GlobalAccessDeniedHandler implements AccessDeniedHandler {
	
	@Override
	public void handle(HttpServletRequest request,
	                   HttpServletResponse response,
	                   AccessDeniedException accessDeniedException) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.OK.value()); // 永远返回200
		ReturnResult<Exception> result = ReturnResult.isFalse("权限不足，无法访问该资源");
		// 使用Jackson序列化
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getOutputStream(), result);
		// 记录日志
		MyLogUtil.error(GlobalAccessDeniedHandler.class,
		                "访问拒绝: 路径={%s}, 原因={%s}, IP={%s}".formatted(
				                request.getServletPath(),
				                accessDeniedException.getMessage(),
				                ElseUtil.getClientIp(request)));
	}
}
