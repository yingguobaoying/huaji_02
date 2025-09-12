package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.UserStatus;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 此接口的操作均会验证管理员身份后才可继续
 */
public interface RootServlet {
	/**
	 * 管理员更改用户头像信息
	 * @param users 更改后的信息(不可改动ID)
	 * @param jpeg 更新的图片,为null时为禁用
	 * @param root 管理员账号
	 * @return 更新后的信息
	 * @throws WriteError 数据库读写错误(小概率)
	 */
	ReturnResult<Users> RootEditUserHeadPortrait(Users users, Users root, MultipartFile jpeg) throws WriteError;

	/**
	 * 管理员更改用户信息
	 * @param users 更改后的信息(不可改动ID)
	 * @param root 管理员账号
	 * @return 更新后的信息
	 * @throws WriteError 数据库读写错误(小概率)
	 */
	ReturnResult<Users> RootEditUserMxg(Users users, Users root) throws WriteError;

	/**
	 * 更改用户的状态
	 * @param users 被更改用户状态的用户
	 * @param root 管理员账号
	 * @param userStatus 新的状态
	 * @return 更新后的信息
	 * @throws WriteError 数据库读写错误(小概率)
	 */
	ReturnResult<Users> RootUpdateUSerStatus(Users users, Users root, UserStatus userStatus) throws WriteError;

	/**
	 * 管理员查询用户
	 * @param usersId 被查询的ID
	 * @param root 管理员
	 * @return 用户信息
	 */
	ReturnResult<Users> RootSelectUserById(Integer usersId, Users root);

	/**
	 * 根据名字查询
	 * @param usersName 用户名/登陆名
	 * @param root 管理员
	 * @return 用户信息
	 */
	ReturnResult<Users> RootSelectUserByName(String usersName, Users root);

	/**
	 * 获取用户列表
	 * @param root 管理员
	 * @return 用户列表
	 */
	ReturnResult<Users> getUserList(Users root);
}
