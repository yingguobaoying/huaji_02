package com.huaji.galgamebyhuaji.Interceptor;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.model.LoginUserDetails;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BasicAuthenticationFilter extends OncePerRequestFilter {
	private final LoginService loginService;
	private final UserMxgServlet userMxgServlet;
	
	public BasicAuthenticationFilter(LoginService loginService, UserMxgServlet userMxgServlet) {
		this.loginService = loginService;
		this.userMxgServlet = userMxgServlet;
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		String uri = request.getServletPath();
		return uri.startsWith("/static/")
		       || uri.startsWith("/public/")
		       || uri.startsWith("/api/login")
		       || uri.startsWith("/api/register");
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                FilterChain chain) throws ServletException, IOException {
		// 检查Session中是否有已认证用户
		HttpSession session = request.getSession(false);
		Users user = null;
		if (session != null)
			user = (Users) session.getAttribute(SystemConstant.JWT_SESSION_USER_NAME);
		// 如果Session中没有用户信息，尝试使用Token进行基础认证
		if (user == null || user == Constant.TOURIST) {
			String token = ElseUtil.getToken(request);
			if (StringUtils.hasText(token)) {
				try {
					UserToken userToken = loginService.loginByToken(token, request);
					if (userToken != null && userToken.getUserId() != null) {
						UsersWithBLOBs users = userMxgServlet.getItselfMxg(userToken.getUserId());
						if (users != null) {
							user = users;
							// 设置Session
							request.getSession(true).setAttribute(SystemConstant.JWT_SESSION_USER_NAME, users);
							// 设置Cookie和Header
							int keepTime = 3600;
							ElseUtil.setUserMxgCookie(response, users, keepTime);
							response.setHeader(SystemConstant.JWT_TOKEN_NAME, userToken.getToken());
							//由于是登录行为因此归类到登录日志里面去
							MyLogUtil.info(LoginService.class,
							               "用户ID为{%d}的用户{%s}在基础过滤器中完成了登录, 登录IP: {%s}".formatted(
									               users.getUserId(),
									               users.getUserName(),
									               ElseUtil.getClientIp(request)));
						}
					}
				} catch (Exception e) {
					// Token认证失败，降级为游客
					user = Constant.TOURIST;
					//由于是登录行为因此归类到登录日志里面去
					MyLogUtil.error(LoginService.class,
					                "用户在基础过滤器中登录失败, 降级为游客, 登录IP: {%s}".formatted(ElseUtil.getClientIp(request)), e);
					//本类的访问记录信息
					MyLogUtil.error(BasicAuthenticationFilter.class,
					                "用户在基础过滤器中登录失败, 降级为游客, 登录IP: {%s}".formatted(ElseUtil.getClientIp(request)), e);
				}
			} else {
				// 没有Token，设置为游客
				user = Constant.TOURIST;
			}
		}
		LoginUserDetails userDetails;
		// 设置认证信息到SecurityContext
		if (user != null && user != Constant.TOURIST) {
			userDetails = new LoginUserDetails(user);
		} else {
			userDetails = new LoginUserDetails(Constant.TOURIST);
		}
		//添加到安全上下文
		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		//设置请求属性
		request.setAttribute(SystemConstant.USER_LOGIN, user != null && user != Constant.TOURIST);
		chain.doFilter(request, response);
	}
}
