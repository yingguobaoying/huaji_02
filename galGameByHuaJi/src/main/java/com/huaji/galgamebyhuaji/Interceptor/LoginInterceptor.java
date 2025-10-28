package com.huaji.galgamebyhuaji.Interceptor;


import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

@Service
public class LoginInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute(SystemConstant.JWT_SESSION_USER_NAME);
		if (Constant.TOURIST.equals(obj))
			return true;
		if (obj instanceof Users users) {
			JurisdictionLevel userRole = JurisdictionLevel.getJurisdiction(users.getJurisdiction());

			if (userRole == JurisdictionLevel.TOURIST_JURISDICTION) {
				// 未登录，允许访问登录/注册页
				return true;
			} else {
				// 已登录，重定向防止重复访问登录页
				request.setAttribute(SystemConstant.SYSTEM_MSG, "您已登录,请勿重复登录");
				return false;
			}
		}
		//没有拿到任何用户说明前一个过滤器出现了错误,用户肯定没有登录,允许访问登陆页面
		return true;
	}
}
