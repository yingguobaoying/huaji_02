package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;

import java.util.List;

/**
 * 此接口不会验证身份,请在调用前验证. 在当前,添加好友的模块并没什么用处.仅作为之后私聊模块的基础
 *
 * @author 滑稽/因果报应
 */
public interface FriendServlet {
	/**
	 * 拒绝好友申请
	 *
	 * @param usersId
	 * 		操作者
	 * @param addFriendUser
	 * 		同意的
	 *
	 * @return
	 */
	Users rejectFriendRequests (Integer usersId, Integer addFriendUser);
	
	/**
	 * 同意好友申请
	 *
	 * @param usersId
	 * 		操作者
	 * @param addFriendUser
	 * 		拒绝的
	 *
	 * @return 关注的人的部分公开信息
	 */
	Users agreeFriendRequests (Integer usersId, Integer addFriendUser);
	
	/**
	 * 添加其他用户好友
	 *
	 * @param usersId
	 * 		操作者
	 * @param addFriendUser
	 * 		被添加的
	 *
	 * @return 关注的人的部分公开信息
	 */
	ReturnResult<Users> addFriend (Integer usersId, Integer addFriendUser);
	
	
	/**
	 * 获取好友列表
	 *
	 * @param usersId
	 * 		获取的用户
	 *
	 * @return 关注列表/申请列表
	 */
	List<Users> getUserFrendList (Integer usersId, boolean isRequest);
}