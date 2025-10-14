package com.huaji.galgamebyhuaji.myUtil;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.entity.Users;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
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
	 *
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
	 *
	 * @return IP地址
	 */
	public static String getClientIp(HttpServletRequest request) {
		// 尝试从 X-Forwarded-For 头部获取 IP 地址
		String ip = request.getHeader("X-Forwarded-For");
		
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			// 如果有多个代理 IP，X-Forwarded-For 中的第一个是客户端的真实 IP
			int index = ip.indexOf(',');
			return index != -1 ? ip.substring(0, index).trim() :
					ip.trim();
		}
		// 检查 X-Real-IP 头
		ip = request.getHeader("X-Real-IP");
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip))
			return ip.trim();
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
		//不再从cookie和session里面获取令牌防止跨站攻击,仅检查请求头和请求体
		return jwt;
	}
	
	/**
	 * 比较两个 IP 是否相同（支持 IPv4、IPv6、本地映射等）
	 *
	 * @param ip1 第一个 IP 地址
	 * @param ip2 第二个 IP 地址
	 *
	 * @return 两个 IP 地址是否逻辑等价
	 */
	public static boolean equalsIp(String ip1, String ip2) {
		if (MyStringUtil.isNull(ip1) || MyStringUtil.isNull(ip2))
			return false;
		// 快速路径：完全相等（常见情况）
		if (ip1.equals(ip2))
			return true;
		// 标准化后比较
		String norm1 = normalizeIp(ip1);
		String norm2 = normalizeIp(ip2);
		// 若标准化后相等则返回 true
		if (norm1.equals(norm2))
			return true;
		// 尝试使用 InetAddress 对比（作为兜底逻辑）
		try {
			InetAddress a1 = InetAddress.getByName(norm1);
			InetAddress a2 = InetAddress.getByName(norm2);
			return a1.equals(a2);
		} catch (UnknownHostException e) {
			// 不抛出，只记录一次 debug 级别日志（频繁调用时不影响性能）
			MyLogUtil.info(ElseUtil.class, "equalsIp(): IP解析失败 [{%s}] vs [{%s}] - {%s}".formatted(ip1, ip2, e.getMessage()));
			return false;
		}
	}
	
	/**
	 * IP 地址标准化
	 * - 支持 IPv4 / IPv6
	 * - 支持 IPv6 映射的 IPv4 (::ffff:192.168.1.1)
	 * - 统一本地地址 (::1, 0:0:0:0:0:0:0:1 → 127.0.0.1)
	 */
	private static String normalizeIp(String ip) {
		if (ip == null) return "";
		
		ip = ip.trim().toLowerCase();
		
		// 处理本地地址
		if ("127.0.0.1".equals(ip) || "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip))
			return "127.0.0.1";
		
		// IPv6 映射的 IPv4 (::ffff:192.168.1.1 → 192.168.1.1)
		if (ip.startsWith("::ffff:")) {
			String mapped = ip.substring(7);
			if (mapped.contains(":")) return ip; // 防止错误切分
			return mapped;
		}
		
		// 对 IPv6 进行完整化标准化（防止 fe80::1 != fe80:0:0:0:0:0:0:1）
		if (ip.contains(":")) {
			try {
				return InetAddress.getByName(ip).getHostAddress().toLowerCase();
			} catch (UnknownHostException e) {
				// 不抛异常，只记录日志并返回原始值
				MyLogUtil.info(ElseUtil.class, "ip转化错误!" + e.getMessage());
				return ip;
			}
		}
		
		// 对 IPv4：去除多余空格和前导0
		if (ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
			String[] segs = ip.split("\\.");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < segs.length; i++) {
				sb.append(Integer.parseInt(segs[i]));
				if (i < segs.length - 1) sb.append('.');
			}
			return sb.toString();
		}
		
		return ip;
	}
}
