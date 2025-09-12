package com.huaji.galgamebyhuaji.myUtil;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.entity.Users;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ElseUtil {
	/**
	 * 设置包含用户基本信息的cook,如果传入空值将设置为游客信息
	 *
	 * @param response 响应体
	 * @param users    设置信息的用户
	 * @param keepTime 保持登录时间,单位:秒
	 */
	public static void setUserMxgCookie(HttpServletResponse response, Users users, int keepTime) {
		if (users == null || Constant.TOURIST.getUserId().equals(users.getUserId()))
			users = Constant.TOURIST;
		response.addCookie(setCook("114514UserName", users.getUserName(), null, keepTime));
		response.addCookie(setCook("114514UserCoin", users.getCoin().toString(), null, keepTime));
		response.addCookie(setCook("114514UserId", users.getUserId().toString(), null, keepTime));
		response.addCookie(setCook("114514UserHeadPortraitUrl", users.getUserHeadPortraitUrl(), null, keepTime));
		response.addCookie(setCook("114514UserJurisdiction", users.getJurisdiction().toString(), null, keepTime));
		response.addCookie(setCook("114514isLogin", Constant.TOURIST != users ? "true" : "false", null, keepTime));
	}
	
	/**
	 * 建立cook信息的简写
	 *
	 * @param name    cookie名称
	 * @param value   内容
	 * @param url     作用路径
	 * @param MaxTime 最大存活时间(秒数)
	 * @return 设置好的cook
	 */
	public static Cookie setCook(String name, String value, String url, Integer MaxTime) {
		if (MyStringUtil.isNull(value)) value = "null";
		if (MyStringUtil.isNull(url)) url = "/";
		String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
		Cookie cookie = new Cookie(name, encodedValue);
		cookie.setPath(url);
		cookie.setMaxAge(MaxTime);
		return cookie;
	}
	
	/**
	 * 尝试获取请求的IP地址
	 *
	 * @param request 请求
	 * @return IP地址
	 */
	public static String getClientIp(HttpServletRequest request) {
		// 尝试从 X-Forwarded-For 头部获取 IP 地址
		String ip = request.getHeader("X-Forwarded-For");
		
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			// 如果有多个代理 IP，X-Forwarded-For 中的第一个是客户端的真实 IP
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index).trim();
			}
			return ip.trim();
		}
		
		// 检查 X-Real-IP 头
		ip = request.getHeader("X-Real-IP");
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			return ip.trim();
		}
		
		// 如果都没有，则使用 getRemoteAddr() 获取 IP
		return request.getRemoteAddr();
	}
	
	public static String getToken(HttpServletRequest request) {
		return getToken(request, SystemConstant.JWT_TOKEN_NAME);
	}
	
	public static String getToken(HttpServletRequest request, String name) {
		if (MyStringUtil.isNull(name)) name = SystemConstant.JWT_TOKEN_NAME;
		String jwt = request.getHeader(name);
		if (MyStringUtil.isNull(jwt)) {
			jwt = (String) request.getAttribute(name);
		}
		if (MyStringUtil.isNull(jwt)) {
			HttpSession session = request.getSession(false);
			if (session != null)
				jwt = (String) session.getAttribute(name);
		}
		if (MyStringUtil.isNull(jwt)) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie c : cookies) {
					if (name.equals(c.getName())) {
						jwt = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
						break;
					}
				}
			}
		}
		return jwt;
	}
}
