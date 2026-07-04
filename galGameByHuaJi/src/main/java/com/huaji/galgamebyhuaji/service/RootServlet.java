package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.UserStatus;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.myUtil.PageUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 此接口的操作均会验证管理员身份后才可继续
 * 此接口设计的所有数据均直接从数据库获取,不会走缓存(将同步更新缓存)
 */
public interface RootServlet {
	
	/**
	 * 管理员更改用户信息
	 *
	 * @param users  更改后的信息(不可改动ID)
	 * @param rootId 管理员账号
	 *
	 * @return 更新后的信息
	 */
	String RootEditUserMxg(UsersWithBLOBs users, int rootId) throws WriteError;
	
	/**
	 * 更改用户的状态
	 *
	 * @param usersId    被更改用户状态的用户
	 * @param rootId     管理员账号
	 * @param userStatus 新的状态
	 *
	 * @return 更新后的信息
	 */
	String RootUpdateUSerStatus(int usersId, int rootId, UserStatus userStatus) throws WriteError;
	
	/**
	 * 管理员查询用户
	 *
	 * @param usersId 被查询的ID
	 * @param rootId  管理员
	 * @return 用户信息
	 */
	UsersWithBLOBs RootSelectUserById(int usersId, int rootId);
	
	/**
	 * 根据名字查询用户
	 *
	 * @param usersName 用户名/登陆名
	 * @param root      管理员
	 *
	 * @return 用户信息
	 */
	List<UsersWithBLOBs> RootSelectUserByName(String usersName, int root);
	
	boolean isIPWhiteList(String ip);
	
	UserToken rootLogin(String token, HttpServletRequest request) throws SessionExceptions;
	
	/**
	 * 特殊root用户初始化方法
	 * 此方法仅在初始化监听器里面使用
	 * 如果在运行时被调用的话好像也没什么问题......
	 * 只会影响已经登录的特殊root,不过由于这两玩意都是维护者在使用,用户看不到,问题不大
	 * 之后考虑使用特殊链接来进行root登录令牌的重置
	 */
	void rootUserInit() throws SessionExceptions;
	
	List<Users> getUserList (PageUtil page);
	
	Integer getUserSize ();
	
}