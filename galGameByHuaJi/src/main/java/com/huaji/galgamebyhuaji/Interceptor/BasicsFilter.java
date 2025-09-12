//package com.huaji.galgamebyhuaji.Interceptor;
//
//import com.huaji.galgamebyhuaji.constant.Constant;
//import com.huaji.galgamebyhuaji.constant.SystemConstant;
//import com.huaji.galgamebyhuaji.entity.UserToken;
//import com.huaji.galgamebyhuaji.entity.Users;
//import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
//import com.huaji.galgamebyhuaji.model.LoginUserDetails;
//import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
//import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
//import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
//import com.huaji.galgamebyhuaji.service.LoginService;
//import com.huaji.galgamebyhuaji.service.UserMxgServlet;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//
//@Component
//public class BasicsFilter extends OncePerRequestFilter {
//	@Autowired
//	private LoginService loginService;
//	@Autowired
//	private UserMxgServlet userMxgServlet;
//
//	@Override
//	protected boolean shouldNotFilter(HttpServletRequest request) {
//		// 跳过预检请求
//		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//			return true;
//		}
//		String uri = request.getServletPath();
//		System.out.println("在BasicsFilter进行了shouldNotFilter判断");
//		return
//				uri.startsWith("/static/")
//						|| uri.startsWith("/public/")
//						|| uri.startsWith("/api/login")
//						|| uri.startsWith("/api/register");
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request,
//	                                HttpServletResponse response,
//	                                FilterChain filterChain)
//			throws ServletException, IOException {
//
//		HttpSession session = request.getSession(true);
//		Users user = (Users) session.getAttribute(SystemConstant.JWT_SESSION_USER_NAME);
//		boolean isLogin = false;
//
//		if (user == null || user == Constant.TOURIST) {
//			String token = ElseUtil.getToken(request);
//			if (!MyStringUtil.isNull(token)) {
//				try {
//					UserToken userToken = loginService.loginByToken(token, request);
//					if (userToken != null && userToken.getUserId() != null) {
//						UsersWithBLOBs users = userMxgServlet.getItselfMxg(userToken.getUserId());
//						if (users != null) {
//							session.setAttribute(SystemConstant.JWT_SESSION_USER_NAME, users);
//							isLogin = true;
//							// 设置 Cookie & JWT
//							int keepTime = 3600; // 秒
//							ElseUtil.setUserMxgCookie(response, users, keepTime);
//							response.setHeader(SystemConstant.JWT_TOKEN_NAME, userToken.getToken());
//							String mxg = (String) request.getAttribute(SystemConstant.SYSTEM_MSG);
//							if (MyStringUtil.isNull(mxg)) {
//								request.setAttribute(SystemConstant.SYSTEM_MSG,
//										"欢迎回来%s!%s".formatted(users.getUserName(), mxg));
//							} else {
//								request.setAttribute(SystemConstant.SYSTEM_MSG, "欢迎回来%s!");
//							}
//							UsernamePasswordAuthenticationToken authentication =
//									new UsernamePasswordAuthenticationToken(
//											users,
//											null,
//											Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 根据你的角色调整
//									);
//							SecurityContextHolder.getContext().setAuthentication(authentication);
//							MyLogUtil.info(BasicsFilter.class,
//									"用户ID为{%d}的用户{%s}在基础过滤器中完成了登录, 登录IP: {%s}".formatted(
//											users.getUserId(),
//											users.getUserName(),
//											ElseUtil.getClientIp(request)
//									));
//						}
//					}
//				} catch (Exception e) {
//					// 降级为游客
//					session.setAttribute(SystemConstant.JWT_SESSION_USER_NAME, Constant.TOURIST);
//					ElseUtil.setUserMxgCookie(response, Constant.TOURIST, 3600);
//					response.setHeader(SystemConstant.JWT_TOKEN_NAME, null);
//					MyLogUtil.error(BasicsFilter.class,
//							"用户在基础过滤器中登录失败, 降级为游客, 登录IP: {%s}".formatted(ElseUtil.getClientIp(request)), e);
//				}
//			} else {
//				// 没有 token，设置为游客
//				session.setAttribute(SystemConstant.JWT_SESSION_USER_NAME, Constant.TOURIST);
//			}
//		} else {
//			isLogin = true;
//			LoginUserDetails userDetails = new LoginUserDetails(user);
//			UsernamePasswordAuthenticationToken authentication =
//					new UsernamePasswordAuthenticationToken(
//							userDetails, null, userDetails.getAuthorities());
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//		}
//
//		request.setAttribute(SystemConstant.USER_LOGIN, isLogin);
//		filterChain.doFilter(request, response);
//	}
//}
