package com.huaji.galgamebyhuaji.Interceptor;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.model.LoginUserDetails;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 这个是游客过滤器,除去需要强认证的路径("/user/")之外其余路径会直接放行, 用户状态可能遭篡改,不过由于大部分数据都是从服务器拿的,前端基本只有数据处 理和展示的功能,所以大部分功能无需在意安全性,因为最多就是数据/页面异常
 *
 * @author HUAJI
 */
@Component
public class TouristFilter extends OncePerRequestFilter {
	
	@Autowired
	LoginService loginService;
	
	@Override
	protected boolean shouldNotFilter (HttpServletRequest request) {
		if ( "OPTIONS".equalsIgnoreCase(request.getMethod()) ) return true;
		System.out.println("在TouristFilter进行了shouldNotFilter判断");
		// 放行不需要强认证的路径
		return !request.getRequestURI().contains("/user/");
		
	}
	
	@Override
	protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String jwt = ElseUtil.getToken(request);
		HttpSession session = request.getSession(false);
		//简单判断
		Users user = session != null ? (Users) session.getAttribute(SystemConstant.JWT_SESSION_USER_NAME) : null;
		boolean isLogin = false;
		if ( MyStringUtil.isNull(jwt) || Constant.TOURIST.equals(user) || user == null || user.getUserId() == null || user.getUserId() == -1 ) {
			request.setAttribute(SystemConstant.SYSTEM_MSG, "请先登录再进行该操作");
			request.setAttribute(SystemConstant.USER_LOGIN, isLogin);
			filterChain.doFilter(request, response);
			return;
		}
		
		// 校验 JWT 有效性
		try {
			user = loginService.testLogin(jwt);
			isLogin = true;
			request.setAttribute(SystemConstant.JWT_TOKEN_NAME, jwt);
			LoginUserDetails userDetails = new LoginUserDetails(user);
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch ( BestException e ) {
			request.setAttribute(SystemConstant.SYSTEM_MSG, "请先登录再进行该操作");
			request.setAttribute(SystemConstant.USER_LOGIN, isLogin);
			MyLogUtil.error(loginService.getClass(), "用户登录验证失败:" + e.getMessage());
			SecurityContextHolder.clearContext();
		} catch ( Exception elseError ) {
			MyLogUtil.error(loginService.getClass(), "用户登录验证时出现异常:" + elseError.getMessage());
			MyLogUtil.error(loginService.getClass(), elseError);
			request.setAttribute(SystemConstant.USER_LOGIN, isLogin);
			SecurityContextHolder.clearContext();
		}
		// 放行
		request.setAttribute(SystemConstant.USER_LOGIN, isLogin);
		filterChain.doFilter(request, response);
	}
}