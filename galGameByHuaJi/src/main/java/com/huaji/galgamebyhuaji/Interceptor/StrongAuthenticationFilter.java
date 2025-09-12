package com.huaji.galgamebyhuaji.Interceptor;

import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.LoginUserDetails;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class StrongAuthenticationFilter extends OncePerRequestFilter {
	
	
	private final UserMxgServlet userMxgServlet;
	
	private final StrongAuthenticationEntryPoint authenticationEntryPoint;
	
	private final TokenService tokenService;
	
	public StrongAuthenticationFilter(TokenService tokenService, UserMxgServlet userMxgServlet, StrongAuthenticationEntryPoint authenticationEntryPoint) {
		this.tokenService = tokenService;
		this.userMxgServlet = userMxgServlet;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		String uri = request.getServletPath();
		return !uri.startsWith("/api/user/");
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                FilterChain chain) throws ServletException, IOException {
		
		String token = ElseUtil.getToken(request);
		if (!StringUtils.hasText(token)) {
			// 没有Token，返回强认证错误
			authenticationEntryPoint.isFalse(request, response,
			                                 new OperationException("您还未登录,请先进行登录后再进行此操作"));
			return;
		}
		
		try {
			UserToken userToken = tokenService.verifyToken(token, -1, ElseUtil.getClientIp(request), false);
			if (userToken == null || userToken.getUserId() == null) {
				throw new AuthenticationServiceException("无效的Token");
			}
			
			//加载用户信息
			UsersWithBLOBs user = userMxgServlet.getItselfMxg(userToken.getUserId());
			if (user == null) {
				throw new UsernameNotFoundException("用户不存在");
			}
			
			// 创建强认证
			LoginUserDetails userDetails = new LoginUserDetails(user);
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			MyLogUtil.info(StrongAuthenticationFilter.class,
			               "用户ID为{%d}的用户{%s}在强认证过滤器中完成了认证, IP: {%s}".formatted(
					               user.getUserId(),
					               user.getUserName(),
					               ElseUtil.getClientIp(request)));
		} catch (OperationException ex) {
			// 认证失败，清理上下文并返回错误
			SecurityContextHolder.clearContext();
			authenticationEntryPoint.isFalse(request, response, ex);
			return;
		} catch (BestException e) {
			SecurityContextHolder.clearContext();
			authenticationEntryPoint.isFalse(request, response, e);
			return;
		} catch (Exception ex) {
			// 其他异常，返回认证错误
			SecurityContextHolder.clearContext();
			authenticationEntryPoint.commence(request, response,
			                                  new AuthenticationServiceException("认证过程中发生错误请稍后再试"));
			MyLogUtil.error(StrongAuthenticationFilter.class, ex);
			return;
		}
		chain.doFilter(request, response);
	}
}
