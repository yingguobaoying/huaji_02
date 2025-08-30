package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.myUtil.PageUtil;

import java.util.List;

/**
 * 此处没有分页操作,也不应该实现,分页应该给前端去做.
 */
public interface SelectServlet {

	/**
	 * 搜索资源
	 *
	 * @param rId   搜索的ID
	 * @param rName 搜索的名称
	 * @param tag      勾选的标签的集合
	 * @param pageMsg  分页信息,为空时代表不进行分页,获取所有记录
	 * @return 搜索到的书籍
	 */
	List<Resources> searchResource(Integer rId, String rName, List<Integer> tag, PageUtil pageMsg);

	/**
	 * 高级检索
	 *
	 * @param rMsg 打包过的检索信息
	 * @param tag     包含的标签
	 * @param pageMsg 分页信息,为空时代表不进行分页,获取所有记录
	 * @return 搜索结果
	 */
	List<Resources> searchResource(Resources rMsg, List<Integer> tag, PageUtil pageMsg);

	/**
	 *  搜索用户
	 * @param uId 用户ID
	 * @param uName 用户名
	 * @param pageMsg 分页信息
	 * @return 搜索结果
	 */
	List<Users> searchUser(Integer uId, String uName, PageUtil pageMsg);


}