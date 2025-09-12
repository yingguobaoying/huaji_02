package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Feedback;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.UserException;

/**
 * 此接口方法均不会验证身份请在使用前验证
 * 并且大部分方法不会做输入验证,请确保输入正确
 */
public interface SecureServlet {
	/**
	 * 冻结用户
	 *
	 * @param user 被冻结者
	 * @return 被冻结的信息
	 */
	String frozenUser(Users user);
	
	/**
	 * 解冻(此方法可由管理员调用,或者发送解冻邮件)
	 *
	 * @param userId 被冻结的用户
	 * @param token    解冻令牌/管理员令牌
	 * @param ip     IP用于检查令牌
	 * @return 解冻成功的用户
	 */
	String unfrozenUser(int userId, String token, String ip) throws SessionExceptions;
	
	/**
	 * 提交反馈信息
	 * @param feedback 反馈信息
	 * @return 反馈信息
	 */
	Feedback feedback(Feedback feedback);
	
	
	/**
	 * 忘记密码
	 *
	 * @return 令牌
	 */
	String forgetPassword(String email,String ip) throws UserException, SessionExceptions;
	
	/**
	 * 更改密码
	 *
	 * @param ip          IP用于检查令牌
	 * @param newPassword 新密码
	 */
	String changePassword(String token,String ip,String newPassword) throws BestException;
	
	String authenticationEmail(String ip, int userId, String token) throws SessionExceptions;
}
