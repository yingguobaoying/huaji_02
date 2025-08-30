package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.entity.Resources;

import java.util.List;

/**
 * 此接口不会验证身份
 *
 * @author 滑稽/因果报应
 */
public interface CollectServlet {
	
	
	/**
	 * 取消收藏资源
	 *
	 * @param usersId
	 * 		收藏者ID
	 * @param resourcesId
	 * 		被取消收藏资源的ID
	 *
	 * @return 收藏资源的信息
	 */
	Resources unCollectResources (Integer usersId, Integer resourcesId);
	
	/**
	 * 搜藏资源
	 *
	 * @param usersId
	 * 		收藏者ID
	 * @param resourcesId
	 * 		被收藏资源的ID
	 *
	 * @return 收藏资源的信息
	 */
	Resources collectResources (Integer usersId, Integer resourcesId);
	
	/**
	 * 获取用户的收藏列表
	 *
	 * @param usersId
	 * 		获取的用户
	 *
	 * @return 收藏列表
	 */
	List<Integer> getCollectResources (Integer usersId);
	
}