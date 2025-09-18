package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.dto.LoginUserMxg;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.SessionService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Controller
@RequestMapping("/api/login")
public class LoginController {
	@Autowired
	private LoginService loginService;
	private final SessionService sessionService;
	@Autowired
	private UserMxgServlet userMxgServlet;
	private static final ConcurrentHashMap<Integer, ReentrantLock> userNameLocks = new ConcurrentHashMap<>();
	
	public LoginController(SessionService sessionService) {
		this.sessionService = sessionService;
	}
	
	
	private ReentrantLock getLockForUserName(String s) {
		int i = s.hashCode();
		return userNameLocks.computeIfAbsent(i, k -> new ReentrantLock());
	}
	
	private void unlockForUserName(ReentrantLock reentrantLock, String s) {
		try {
			// 只在当前线程持有锁的情况下释放
			if (reentrantLock.isHeldByCurrentThread()) {
				reentrantLock.unlock();
			}
		} finally {
			// 确保没有其他线程持有再移除
			if (!reentrantLock.isLocked() && !reentrantLock.hasQueuedThreads()) {
				int i = s.hashCode();
				userNameLocks.remove(i, reentrantLock);
			}
		}
	}
	
	@GetMapping("/loginByToken")
	@ResponseBody
	@Transactional
	public ReturnResult<UsersWithBLOBs> loginByToken(
			@RequestParam(value = "token", required = false) String usersToken,
			HttpServletRequest request,
			HttpServletResponse response) throws BestException {
		// 若 token 为空，尝试从 request 中获取
		if (MyStringUtil.isNull(usersToken))
			usersToken = ElseUtil.getToken(request);
		// 若仍为空，抛出异常
		if (MyStringUtil.isNull(usersToken))
			throw new OperationException("登录失败，因为未提供有效的token。请稍后重试或重新登录。");
		
		// 通过 token 登录，获取用户 token 对象
		UserToken usersTokenObj = loginService.loginByToken(usersToken, request);
		if (usersTokenObj == null || usersTokenObj.getUserId() == null)
			throw new OperationException("登录失败，token无效或已过期。请重新登录以继续访问。");
		// 获取用户详细信息（包括 BLOB 字段）
		UsersWithBLOBs users = userMxgServlet.getItselfMxg(usersTokenObj.getUserId());
		if (users == null) {
			throw new OperationException("登录失败，未找到对应用户信息。请检查账号是否正确。");
		}
		// 设置 session 属性
		
		// 设置 Cookie 的过期时间为 1 小时
		int keepTime = 60 * 60; // 秒
		request.getSession(true).setAttribute(SystemConstant.JWT_SESSION_USER_NAME, users);
		//设置用户信息cookie
		ElseUtil.setUserMxgCookie(response, users, keepTime);
		//设置jwt
		response.setHeader(SystemConstant.JWT_TOKEN_NAME, usersTokenObj.getToken());
		return getUsersReturnResult(request, users);
	}
	
	private ReturnResult<UsersWithBLOBs> getUsersReturnResult(HttpServletRequest request, UsersWithBLOBs users) {
		String tip;
		String attribute = (String) request.getAttribute(SystemConstant.SYSTEM_MSG);
		if (!MyStringUtil.isNull(attribute)) {
			tip = "登录成功!\n欢迎回来:%s\n今日签到情况:%s".formatted(users.getUserName(), attribute);
		}
		else {
			tip = "登录成功!\n欢迎回来:%s".formatted(users.getUserName());
		}
		request.removeAttribute(SystemConstant.SYSTEM_MSG);
		return ReturnResult.isTrue(tip, users);
	}
	
	
	@PostMapping("/userLogin")
	@ResponseBody
	@Transactional
	public ReturnResult<UsersWithBLOBs> userLogin(
			@Valid @RequestBody
			LoginUserMxg userMxg,
			BindingResult testResult,
			HttpServletRequest request,
			HttpServletResponse response
	) throws BestException {
		if (testResult.hasErrors())
			if (testResult.getFieldError() == null)
				return ReturnResult.isFalse(testResult.getFieldError().getField());
			else
				return ReturnResult.isFalse("未知错误，请稍后重试。");
		//打包
		UsersWithBLOBs users = userMxg.getUsers();
		Integer keepTime = userMxg.getKeepTime();
		Boolean isDay = userMxg.getUnitIsDay();
		//设置登录时间
		long time;
		if (keepTime != null && keepTime > 0) {
			if (isDay != null && isDay) {
				keepTime = Math.min(30, keepTime);
				time = TimeUnit.DAYS.toMillis(keepTime);
				keepTime *= 24 * 60 * 60;
			}
			else {
				time = TimeUnit.HOURS.toMillis(keepTime);
				keepTime *= 60 * 60;
			}
		}
		else {
			time = TimeUnit.HOURS.toMillis(1);
			keepTime = 60 * 60;
		}
		//执行登录操作
		ReentrantLock lock = getLockForUserName(userMxg.getUserName() + "_" + userMxg.getUserPassword());
		try {
			lock.lock();
			UserToken login = loginService.login(users, request, time);
			userMxg.setUserPassword(null);
			//设置cookie
			users = userMxgServlet.getItselfMxg(login.getUserId());
			String jwt = login.getToken();
			request.getSession(true).setAttribute(SystemConstant.JWT_SESSION_USER_NAME, users);
			//设置用户信息cookie
			ElseUtil.setUserMxgCookie(response, users, keepTime);
			//设置jwt
			response.setHeader(SystemConstant.JWT_TOKEN_NAME, jwt);
			return getUsersReturnResult(request, users);
		} finally {
			unlockForUserName(lock, userMxg.getUserName() + "_" + userMxg.getUserPassword());
		}
	}
	
	@PostMapping("/userRegister")
	@ResponseBody
	@Transactional
	public ReturnResult<Users> userRegister(
			@Valid @RequestBody UsersWithBLOBs usersMxg,
			BindingResult testResult
	) {
		if (testResult.hasErrors())
			if (testResult.getFieldError() == null)
				return ReturnResult.isFalse(testResult.getFieldError().getField());
			else
				return ReturnResult.isFalse("未知错误，请稍后重试。");
		testUserMsgIsNull(usersMxg);
		System.out.println(usersMxg);
		String s = usersMxg.getMailbox() + usersMxg.getMailbox() + usersMxg.getUserPe();
		ReentrantLock lockForUserName = getLockForUserName(s);
		try {
			lockForUserName.lock();
			return loginService.register(usersMxg);
		} finally {
			unlockForUserName(lockForUserName, s);
		}
	}
	
	@GetMapping("/userExit")
	@ResponseBody
	@Transactional
	public ReturnResult<Users> exit(
			HttpServletRequest request,
			HttpServletResponse response
	) throws BestException {
		String token = ElseUtil.getToken(request);
		Users users = loginService.testLogin(token);
		//销毁token
		String s = null;
		try {
			s = sessionService.exitLogin(users.getUserId(), false, token);
		} catch (SessionExceptions e) {
			ErrorEnum error = ErrorEnum.getError(e.getErrorType(), e.getErrorNum());
			if (!ErrorEnum.SESSION_NOT_AVAILABLE_ERROR.equals(error)) throw e;
			s = "您已经退出登录，请勿重复操作。";
		}
		//设置用户信息cookie
		ElseUtil.setUserMxgCookie(response, Constant.TOURIST, 3600);
		//设置清除信息
		response.setHeader(SystemConstant.JWT_TOKEN_NAME, null);
		SecurityContextHolder.clearContext();
		return ReturnResult.isTrue(s, users);
	}
	
	@PostMapping("/testRegisterMxg")
	@ResponseBody
	public ReturnResult<Users> testRegisterMxg(
			@RequestBody Users usersMxg
	) {
		return loginService.testRegisterMxg(usersMxg, null);
	}
	
	private void testUserMsgIsNull(UsersWithBLOBs users) {
		if (users == null) throw new OperationException("用户信息不存在，请填写完整的注册信息。");
		if (MyStringUtil.isNull(users.getUserNameLogin()))
			throw new OperationException("用户登录名不可为空，请填写登录名。");
		if (MyStringUtil.isNull(users.getUserPassword()))
			throw new OperationException("用户密码不可为空，请设置登录密码");
		if (MyStringUtil.isNull(users.getMailbox())) throw new OperationException("邮箱不可为空，请填写邮箱地址。");
		if (MyStringUtil.isNull(users.getUserName()))
			throw new OperationException("用户名不可为空，请设置您的昵称或用户名。");
	}
	
}
