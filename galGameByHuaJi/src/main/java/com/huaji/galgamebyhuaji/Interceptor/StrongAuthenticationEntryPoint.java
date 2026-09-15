package com.huaji.galgamebyhuaji.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class StrongAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        
        ReturnResult<Exception> byd = ReturnResult.isFalse("");
        if (authException instanceof InsufficientAuthenticationException) {
            byd.setMsg("您的权限不足,无法进行此操作");
        } else if (authException instanceof AuthenticationServiceException ||
                   authException instanceof BadCredentialsException) {
            byd.operationError("Token无效或验证失败,请重新登录后重试", null, 2007);
        } else {
            byd.setMsg(authException.getMessage());
        }
        // 使用Jackson序列化
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), byd);
        // 记录日志
        log.error("强认证失败: 路径={}, 原因={}, IP={}",
                  request.getServletPath(), authException.getMessage(), ElseUtil.getClientIp(request));
    }
}
