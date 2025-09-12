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
	 * 高级检索
	 *
	 * @param rMsg    打包过的检索信息
	 * @param tag     包含的标签
	 * @param tagSize
	 * @param pageMsg 分页信息,为空时代表不进行分页,获取所有记录
	 * @return 搜索结果
	 */
	List<Resources> searchResource(Resources rMsg, List<Integer> tag, int tagSize, PageUtil pageMsg);
	
	int getSearchResourceSize(Resources rMsg, List<Integer> tag, int tagSize);
	
	/**
	 * 搜索用户
	 *
	 * @param uId     用户ID
	 * @param uName   用户名
	 * @param pageMsg 分页信息
	 * @return 搜索结果
	 */
	List<Users> searchUser(Integer uId, String uName, PageUtil pageMsg);
	
	
}
