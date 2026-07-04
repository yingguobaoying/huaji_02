package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.dto.UserMxgWithOldUserMxg;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 请注意此接口大部分操作均不会进行权限检查,请在调用前进行检查(如果有例外将会注明)
 */
public interface UserMxgServlet {
	/**
	 * 更新用户信息
	 *
	 * @param users 更新后的用户信息(不包括头像)
	 *
	 * @return 更新后的信息
	 */
	UsersWithBLOBs updateUsers(UserMxgWithOldUserMxg users);
	
	/**
	 * 更新用户头像
	 *
	 * @param croppedImage    图片
	 * @param userId          更改头像的用户
	 * @param banHeadPortrait 是否禁用头像
	 *
	 * @return 更新结果信息
	 */
	String updateUserHeadPortraitUrl(MultipartFile croppedImage, int userId, boolean banHeadPortrait) throws  IOException;
	
//	/**
//	 * 删除用户信息,如果身份验证失败仅会记录日志(发生自定义异常时也是)和返回错误信息,
//	 * <p>请注意:</p>
//	 * <li>不能删除权限比你高的用户</li>
//	 * <li>ID为0和1的用户为特殊root用户不可以删除(自己操作自己除外,不过不建议因为一些初始化操作时默认这两个用户存在)</li>
//	 * <li>此操作会尝试验证身份</li>
//	 *
//	 * @param rootId    操作者
//	 * @param usersId   根据用户ID进行,请确保用户ID正确
//	 * @param userToken 用户token
//	 *
//	 * @return 被删除的信息
//	 *
//	 * @throws WriteError 数据读写错误(低概率)
//	 */
//	ReturnResult<Users> deleteUsers(int rootId, int usersId, String userToken) throws BestException;
	
	/**
	 * 查询某位用户的公开信息
	 *
	 * @param usersId 被查询者ID
	 *
	 * @return 返回用户, 不存在时操作失败
	 */
	UsersWithBLOBs selectUsers(Integer usersId);
	
	/**
	 * 一次性获取所有用户的列表信息用于展示用户列表,此方法仅建议在服务器初始化时调用,后续直接从缓存中获取
	 */
	void getAllUserListMxg();
	
	/**
	 * 获取除去密码外的所有个人信息
	 *
	 * @param usersId 获取的信息
	 *
	 * @return 获取的结果
	 */
	UsersWithBLOBs getItselfMxg(Integer usersId);
	
	Users getUserListMsg(Integer usersId);
	
	Users getUSerMsgByEmail(String email);
	
	String RootEditUserHeadPortrait (int usersId, int rootId, MultipartFile jpeg) throws WriteError, IOException;
}