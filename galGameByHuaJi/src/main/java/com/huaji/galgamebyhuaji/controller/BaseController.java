package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.LoginUserDetails;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
	/**
	 * 请注意,非"/user"路径获取的该值是不准确的(因为JWT没有验证过)
	 */
	protected boolean getLoginStatus (HttpServletRequest request) {
		return Boolean.TRUE.equals(request.getAttribute(SystemConstant.USER_LOGIN));
	}
	
	/**
	 * 获取/设置消息
	 *
	 * @param msg
	 * 		设置消息,为空时仅获取
	 *
	 * @return request里面的系统消息
	 */
	@Nullable
	protected String getMsg (HttpServletRequest request, String msg) {
		if ( !MyStringUtil.isNull(msg) ) {
			request.setAttribute(SystemConstant.SYSTEM_MSG, msg);
		}
		return (String) request.getAttribute(SystemConstant.SYSTEM_MSG);
		
	}
	
	/**
	 * 获取消息
	 *
	 * @return request里面的系统消息
	 */
	@Nullable
	protected String getMsg (HttpServletRequest request) {
		return (String) request.getAttribute(SystemConstant.SYSTEM_MSG);
		
	}
	
	/**
	 * 获取登录的用户,无上下文时,返回游客
	 */
	protected Users getLoginUser () {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( authentication != null && authentication.getPrincipal() instanceof LoginUserDetails userDetails ) {
			if ( userDetails.getUser() != null ) {
				return userDetails.getUser();
			}
		}
		return Constant.TOURIST;
	}
	
	/**
	 * 获取获取登录的用户,无上下文时,返回游客
	 */
	protected Users getLoginUser (boolean isThrow) {
		Users loginUser = getLoginUser();
		if ( isThrow && Constant.TOURIST.equals(loginUser) )
			throw new OperationException("请先登录再进行此操作!");
		return loginUser;
	}
}