package com.huaji.galgamebyhuaji.vo;

import jakarta.validation.constraints.NotNull;

/**
 * @author 滑稽/因果报应
 */
public class DataWithUserMsg<T> {

	private String userName;
	private String userHeadPortraitUrl;
	private T data;

	public DataWithUserMsg<T> setUserMxg(@NotNull int userId) {
		//todo 重构完成后需要补充
		return null;
	}


	public void setData(T data) {
		this.data = data;
	}

}