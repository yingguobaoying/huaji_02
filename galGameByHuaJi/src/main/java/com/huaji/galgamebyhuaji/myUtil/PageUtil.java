package com.huaji.galgamebyhuaji.myUtil;

/**
 * 分页记录打包类(方便获取起止下标)
 */
public class PageUtil {

	// 当前页显示记录条数
	private int size;

	// 当前页码（从1开始）
	private int page;

	/**
	 * 构造方法
	 *
	 * @param size 每页条数
	 * @param page 当前页码（从1开始）
	 */
	public PageUtil(int size, int page) {
		this.size = size;
		this.page = page;
	}

	/**
	 * 获取开始下标（通常用于数据库中的 LIMIT 子句）
	 *
	 * @return 当前页的开始下标（从0开始）
	 */
	public int getStartIndex() {
		return (page - 1) * size;
	}

	/**
	 * 获取当前页的结束下标（非包含式，可用于内存分页）
	 *
	 * @return 当前页的结束下标
	 */
	public int getEndIndex() {
		return page * size;
	}


	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
