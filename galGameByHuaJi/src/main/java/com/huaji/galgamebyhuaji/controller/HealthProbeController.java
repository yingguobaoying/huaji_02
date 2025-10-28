package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.Interceptor.StrongAuthenticationFilter;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;

@Controller
@RequiredArgsConstructor
public class HealthProbeController {
	final StrongAuthenticationFilter StrongAuthenticationEntryPoint;
	
	//当出现错误无法被spring security 捕抓时作为备用手段
	@GetMapping(path = {"/api/error/dispose", "/error"})
	@ResponseBody
	public ReturnResult<String> error(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.OK.value());
		Exception ex = (Exception) request.getAttribute("securityException");
		Iterator<String> iterator = request.getAttributeNames().asIterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			if (request.getAttribute(next) instanceof Exception e) {
				ex = e;
				break;
			}
		}
		if (ex == null) {
			ReturnResult<String> error = ReturnResult.isError("未知错误");
			String attribute = (String) request.getAttribute(SystemConstant.SYSTEM_MSG);
			if (MyStringUtil.isNull(attribute)) {
				error.addMap(SystemConstant.SYSTEM_MSG, attribute);
			}
			return error;
		}
		ReturnResult<String> error;
		if (ex instanceof OperationException) {
			error = ReturnResult.isFalse(((OperationException) ex).getMsg());
		} else if (ex instanceof InsufficientAuthenticationException) {
			error = ReturnResult.isFalse("您的权限不足,无法进行此操作");
		} else if (ex instanceof AuthenticationServiceException ||
		           ex instanceof BadCredentialsException) {
			error = ReturnResult.isFalse("Token无效或验证失败,请重新登录后重试");
		} else if (ex instanceof AuthenticationException authException) {
			error = ReturnResult.isFalse(authException.getMessage());
		} else {
			error = ReturnResult.isError(ex.getMessage(), ex.getMessage());
		}
		String attribute = (String) request.getAttribute(SystemConstant.SYSTEM_MSG);
		if (MyStringUtil.isNull(attribute)) {
			error.addMap(SystemConstant.SYSTEM_MSG, attribute);
		}
		return error;
	}
	
	//上线时请保留此类用于服务器链接测试,相当与web版本的ping
	//todo:请只保留test(),用于当一个服务器探针,其他请删除掉!
	@GetMapping("/api/user/test/root")
	@ResponseBody
	@PreAuthorize("hasRole('ROOT_JURISDICTION')")
	public String testROOT_JURISDICTION() {
		return "test";
	}
	
	@GetMapping("/api/user/test/admin")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN_JURISDICTION')")
	public String testADMIN_JURISDICTION() {
		return "test";
	}
	
	@GetMapping("/api/user/test/rAdmin")
	@ResponseBody
	@PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
	public String testRESOURCES_ADMIN_JURISDICTION() {
		return "test";
	}
	
	@GetMapping("/api/user/test/user")
	@ResponseBody
	@PreAuthorize("hasRole('USERS_JURISDICTION')")
	public String testUSERS_JURISDICTION() {
		return "test";
	}
	
	@GetMapping("/api/user/test/else")
	@ResponseBody
	@PreAuthorize("hasRole('NOT_VALIDATED')")
	public String testNOT_VALIDATED() {
		return "test";
	}
	
	@GetMapping("/api/test")
	@ResponseBody
	public String test() {
		return "test";
	}
}
