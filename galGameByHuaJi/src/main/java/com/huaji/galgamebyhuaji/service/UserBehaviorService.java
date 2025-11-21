package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Links;
import com.huaji.galgamebyhuaji.entity.UserResourceRepository;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vo.CommentWithUser;

import java.util.List;

/**
 * 请注意此接口所有操作均不会进行权限检查,请在调用前进行检查
 */
public interface UserBehaviorService {
	public ReturnResult<UserResourceRepository> getUserResource(Integer userId,Integer rId);
	
	/**
	 * 使用积分换取下载连接,需要用户积分足够,此处连接为服务器提供的本地下载价格稍高
	 *
	 * @param userId 购买用户
	 * @param rId    购买资源id
	 *
	 * @return 生成的令牌
	 */
	ReturnResult<String> buyDown(Integer userId, Integer rId) ;
	
	/**
	 * 使用积分换取下载连接,需要用户积分足够,此处连接为外部网盘
	 *
	 * @param userId 购买用户
	 * @param rId    购买资源id
	 *
	 * @return 生成的令牌
	 */
	ReturnResult<String> buyOutsideDown(Integer userId, Integer rId) ;
	
	ReturnResult<String> buyAll(Integer userId, Integer rId) ;
	
	/**
	 * 获取用户提供的下载连接
	 *
	 * @param users 提供的用户
	 *
	 * @return 提供的连接
	 */
	List<Links> getUserUpLink(Users users);
	
	/**
	 * 获取用户评论
	 *
	 * @param users 用户
	 *
	 * @return 用户评论(为回复时仅获取上一级评论以及同级评论信息)
	 */
	List<CommentWithUser> getUserComment(Users users);
}
