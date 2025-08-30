package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Links;
import com.huaji.galgamebyhuaji.entity.LinksWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.LinksEnum;
import com.huaji.galgamebyhuaji.exceptions.WriteError;

import java.util.List;
import java.util.Map;

/**
 * 请注意此接口所有操作均不会进行权限检查,请在调用前进行检查
 */
public interface LinkService {
	/**
	 * 获取外部连接
	 *
	 * @param rId
	 * 		资源ID
	 * @param isAll
	 * 		是否获取所有连接 true:  是 false :仅可用
	 * @param isAbout
	 * 		是否为自动获取
	 * @param userId
	 * 		用户ID
	 *
	 * @return 所有连接/可用连接
	 */
	List<LinksWithBLOBs> getLink (Integer rId, boolean isAll, int userId, boolean isAbout) throws WriteError;
	
	/**
	 * 添加连接信息
	 *
	 * @param link
	 * 		新信息
	 *
	 * @return 添加的信息
	 *
	 */
	LinksWithBLOBs addLink (LinksWithBLOBs link) throws WriteError;
	
	/**
	 * 删除信息
	 *
	 * @param linkId
	 * 		对应ID
	 *
	 * @return 被删除的信息
	 *
	 */
	Links dleLink (Long linkId) throws WriteError;
	
	/**
	 * 根据用户获取所有链接
	 *
	 * @param userId
	 * 		用户ID
	 * @param isRoot
	 * 		是否启用管理员操作,为true时额外验证管理员身份,成功后返回所有信息
	 *
	 * @return 连接信息
	 */
	LinksWithBLOBs getLinks (Integer userId, boolean isRoot);
	
	/**
	 * 更新连接
	 *
	 * @param link
	 * 		更新后的连接
	 *
	 * @return 更新后的连接
	 *
	 */
	Links upDateLink (LinksWithBLOBs link) throws WriteError;
	
	/**
	 * 获取连接统计
	 *
	 * @return Map<Integer, Integer> key:资源ID value:可用链接数量
	 */
	Map<Integer, Integer> getStatisticsMxg ();
	
	/**
	 * 修改链接稳定性(最低为不稳定,仅管理员可以确认连接不可用)
	 *
	 * @param linkId
	 * 		连接ID
	 * @param isRoot
	 * 		是否管理员
	 *
	 * @return 操作结果
	 */
	String changeLinkStability (Long linkId, boolean isRoot, LinksEnum level);
}