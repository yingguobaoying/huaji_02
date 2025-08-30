package com.huaji.galgamebyhuaji.config;

import com.huaji.galgamebyhuaji.myUtil.JWTUtil;
import com.huaji.galgamebyhuaji.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	private final SessionService sessionService;

	public JwtAuthenticationFilter(JWTUtil jwtUtil, SessionService sessionService) {
		this.jwtUtil = jwtUtil;
		this.sessionService = sessionService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

//			ReturnResult<LoginUserDTO> result = jwtUtil.parseToken(token, "user", LoginUserDTO.class);
//			if (result.isOperationResult()) {
//				LoginUserDTO user = result.getReturnResult();
//				String userIp = request.getRemoteAddr();
//
//				// 查数据库的 Session 状态
//				Session session = sessionService.getUserSession(user.getUserId());
//				if (session == null || !session.getStatus().equals("ONLINE")) {
//					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//					response.getWriter().write("登录状态已失效，请重新登录");
//					return;
//				}
//
//				// 异地登录判断
//				if (!userIp.equals(session.getLastLoginIp())) {
//					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//					response.getWriter().write("账号在其他地点登录，已强制下线");
//					return;
//				}
//
//				// Token 自动续期（如果快到期）
//				if (jwtUtil.isAboutToExpire(token)) {
//					sessionService.renewSession(user.getUserId()); // 续期 session 表记录
//					String newToken = jwtUtil.generateToken("user", user, jwtUtil.getJwtConfig().getExpirationTime());
//					response.setHeader("X-Refresh-Token", newToken); // 或写入 cookie
//				}
//
//				// 构造 Spring Security 的认证对象
//				List<GrantedAuthority> authorities = user.getRoles().stream()
//						.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//						.toList();
//
//				UsernamePasswordAuthenticationToken authentication =
//						new UsernamePasswordAuthenticationToken(user, null, authorities);
//
//				SecurityContextHolder.getContext().setAuthentication(authentication);
//			}
		}

		filterChain.doFilter(request, response);
	}
}