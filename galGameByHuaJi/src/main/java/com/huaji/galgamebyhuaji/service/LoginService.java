package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 此接口为登陆验证接口
 * <p>
 * 使用此类的前提:用户数据已经通过基本校验,密码已经经过加密
 * 此接口的所有认证方法都会检查用户会话状态
 * </p>
 */
public interface LoginService {
	/**
	 * 用户登陆方法
	 *
	 * @param users    需要登陆的用户(需要3个登陆凭据和密码均不为空),登陆凭据最好是重复的 比较用户一次只能使用一个作为凭据输入
	 * @param KeepTime 保持登录的时间
	 * @return 登陆成功的用户信息
	 *
	 * @throws BestException 用户重复(概率)
	 * @throws WriteError    数据读写错误(低概率)
	 */
	UserToken login(UsersWithBLOBs users, HttpServletRequest request, Long KeepTime) throws BestException, WriteError;
	
	/**
	 * 注册方法
	 *
	 * @param users 需要注册的用户
	 * @return 注册成功的用户信息
	 *
	 * @throws WriteError 数据读写错误(低概率)
	 */
	ReturnResult<Users> register(UsersWithBLOBs users) throws WriteError;
	
	/**
	 * 仅检查用户登陆状态(该操作会检查传入用户的token信息)
	 *
	 * @param usersToken 需要检查的用户token
	 * @return 正确信息
	 *
	 * @throws BestException 用户重复
	 */
	Users testLogin(String usersToken) throws BestException;
	
	/**
	 * 检查用户登陆状态和权限(该操作会检查传入用户的token信息)
	 *
	 * @param usersToken   需要检查的用户token
	 * @param jurisdiction 需要的用户权限
	 * @return 正确信息
	 *
	 * @throws BestException 用户重复
	 */
	Users testLogin(String usersToken, int jurisdiction) throws BestException;
	
	
	/**
	 * 用户签到方法
	 *
	 * @param userId 需要签到的用户ID，用于识别用户身份
	 * @return 签到成功返回签到信息，签到奖励提示信息
	 *
	 * @throws SessionExceptions 当用户会话无效或用户不存在时抛出
	 */
	String ClockIn(Integer userId) throws SessionExceptions;
	
	
	/**
	 * 检查用户信息的方法
	 *
	 * @param users      需要检查的用户信息对象，包含需要验证的用户属性
	 * @param testUserId 需要检查的用户ID
	 * @return 检查结果, 未通过时:用户属性不为空的那一项为重复项
	 */
	ReturnResult<Users> testRegisterMxg(Users users, Integer testUserId);
	
	/**
	 * 使用token进行登录
	 *
	 * @param token 用户登录使用的token
	 * @return 正确信息
	 */
	 UserToken loginByToken(String token, HttpServletRequest request) throws BestException;
	
	/**
	 * 判断用户是否拥有权限
 	 * @param userId 用户ID
	 * @param jurisdictionLevel 需要的最低权限
	 * @return 判断结果,false = 无权限
	 */
	boolean userHasJurisdiction (int userId, JurisdictionLevel jurisdictionLevel);
}