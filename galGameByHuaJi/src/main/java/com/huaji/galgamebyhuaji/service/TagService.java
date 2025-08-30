package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Tag;
import com.huaji.galgamebyhuaji.model.ReturnResult;

import java.util.Map;

/**
 * 请注意此接口所有操作均不会进行权限检查,请在调用前进行检查
 */
public interface TagService {
	/**
	 * 添加tag,添加之前需要确保子tag和父tag正确
	 * @param tag 添加的信息tag
	 * @return 成功添加的tag
	 * @ 数据读写错误(低概率)
	 */
	ReturnResult<Tag> addTag(Tag tag) ;

	/**
	 * @param tag 修改后的tag
	 * @return 修改后的tag
	 * @ 数据读写错误(低概率)
	 */
	ReturnResult<Tag> updateTag(Tag tag) ;

	/**
	 *
	 * @param id tag的ID,根据该ID删除
	 * @return 被删除的tag
	 * @ 数据读写错误(低概率)
	 */
	ReturnResult<Tag> deleteTag(Integer id) ;

	/**
	 * 获取所有tag信息,获取的tag是已经打包好的(父子关系正确),请注意此方法会更新缓存包中的tag集合数据(将Map清空并从数据中重新读取)
	 * @return 一个包含所有tag的Map, 以其ID作为主键
	 */
	Map<Integer, Tag> getTagMap();
}