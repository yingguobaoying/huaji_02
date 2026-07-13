package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.LogUtil;
import com.huaji.galgamebyhuaji.myUtil.PageUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.RootServlet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user/manage")
@Slf4j
@RequiredArgsConstructor
public class UserManageController extends BaseController {
	final LoginService loginService;
	private final RootServlet rootServlet;
	
	private Users getUser () {
		Users user = getLoginUser();
		if ( !loginService.userHasJurisdiction(user.getUserId(), JurisdictionLevel.ADMIN_JURISDICTION) )
			throw new OperationException("权限不足,如需进行操作请联系管理员");
		return user;
	}
	
	@GetMapping("/getUserList/{page}")
	public ReturnResult<Users> getUserList (@PathVariable("page") int page) {
		Users user = getUser();
		LogUtil.RootBehaviorLog(log, "获取用户列表,当前页数{%s}".formatted(page), user);
		return ReturnResult.isTrue("用户列表获取成功", rootServlet.getUserList(new PageUtil(10, page)), null);
	}
	
	@GetMapping("/getUserSize")
	public ReturnResult<Integer> getUserSize () {
		Users user = getUser();
		LogUtil.RootBehaviorLog(log, "获取用户总数量", user);
		return ReturnResult.isTrue("用户数目获取成功", rootServlet.getUserSize());
	}
	
	@GetMapping("/getUserInfo/{id}")
	public ReturnResult<UsersWithBLOBs> getUserInfo (@PathVariable("id") int id) {
		Users user = getUser();
		LogUtil.RootBehaviorLog(log, "获取用户信息,用户id{%s}".formatted(id), user);
		return ReturnResult.isTrue("用户信息获取成功", rootServlet.RootSelectUserById(id, user.getUserId()));
	}
	
}
