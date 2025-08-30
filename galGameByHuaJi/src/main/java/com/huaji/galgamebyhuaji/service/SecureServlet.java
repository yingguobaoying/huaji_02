package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Feedback;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;

/**
 * 此接口方法均不会验证身份请在使用前验证
 */
public interface SecureServlet {
	/**
	 * 冻结用户
	 * @param user 被冻结者
	 * @return 被冻结的信息
	 */
	ReturnResult<Users> frozenUser(Users user);

	/**
	 * 解冻(此方法可由管理员调用,或者发送解冻邮件)
	 * @param user 被冻结的用户
	 * @param Mxg 解冻令牌
	 * @return 解冻成功的用户
	 */
	ReturnResult<Users> unfrozenUser(Users user, String Mxg);

	/**
	 * 提交反馈信息
	 * @param feedback 反馈信息
	 * @return 反馈信息
	 */
	ReturnResult<Feedback> feedback(Feedback feedback);


	/**
	 * 忘记密码,此方法将同步更新数据库信息
	 * @param users 忘记密码用户
	 * @param newPassword 新密码
	 * @return 新的用户信息
	 */
	ReturnResult<Users> forgetPassword(Users users, String newPassword);
}