package com.huaji.galgamebyhuaji.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaji.galgamebyhuaji.model.LoginUserDetails;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
	                   AccessDeniedException accessDeniedException) throws IOException {
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.FORBIDDEN.value());
		
		ReturnResult r = ReturnResult.isFalse("您的权限不足,无法访问该选项");
		// 获取当前用户信息
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof LoginUserDetails) {
			LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getOutputStream(), r);
		
		// 记录日志
		MyLogUtil.error(CustomAccessDeniedHandler.class,
		                "访问被拒绝: 路径={%s}, 用户={%s}, IP={%s}".formatted(
				                request.getServletPath(),
				                authentication != null ? authentication.getName() : "anonymous(游客)",
				                ElseUtil.getClientIp(request)));
	}
}
