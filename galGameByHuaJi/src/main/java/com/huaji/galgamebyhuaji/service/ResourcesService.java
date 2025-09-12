package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.ResourcesFileMap;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vo.SelectViewMag;

import java.util.List;

/**
 * 请注意此接口所有操作均不会进行权限检查,请在调用前进行检查
 */
public interface ResourcesService {
	/**
	 *添加资源信息,会进行重复检查
	 * @param resources 添加的资源,需要会更新资源和其中包括的tag
	 * @return 添加的资源
	 * @throws WriteError 数据读写错误(低概率)
	 */
	ReturnResult<Resources> addResources(Resources resources) throws WriteError;

	/**
	 *更新资源信息(仅更新不为空的位置),会进行重复检查
	 * @param resources 更新后的资源,请确保ID存在且为需要修改的值
	 * @return 更新后的资源
	 * @throws WriteError 数据读写错误(低概率)
	 */
	ReturnResult<Resources> updateResources(Resources resources) throws WriteError;

	/**
	 *
	 * @param rId 需要删除的资源ID
	 * @return 被删除的资源
	 * @throws WriteError 数据读写错误(低概率)
	 */
	ReturnResult<Resources> deleteResources(Integer rId) throws WriteError;

	/**
	 * 获取所有资源信息,此方法会同步更新缓存所有内容(从数据读取后覆盖)
	 * 由于此处会向缓存中读取tag数据,所以需要确保tag接口的方法已经执行过了
	 * @return 重新读取的信息
	 */
	List<Resources> getAllResources();

	/**
	 * 获取资源的全局统计信息
	 * @return 打包好的资源统计信息
	 */
	ReturnResult<SelectViewMag> getResourceStatics();

	/**
	 * 获取单个资源
	 * @param rId 需要获取的资源的ID
	 * @return 对应的资源
	 */
	Resources getResource(int rId);

	/**
	 * 获取资源列表集合
	 * @return 资源列表
	 */
	List<Resources> getResourceList(int start, int end);

	Integer getResourceListSize();

	List<ResourcesFileMap> getResourceFileList(int rId);
}
