package com.huaji.galgamebyhuaji.vo;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 滑稽/因果报应
 */
@Getter
@Setter
public class DataWithUserMsg<T> {
	
	private String userName;
	private String userHeadPortraitUrl;
	private T data;
	
	public DataWithUserMsg<T> setUserMxg(int userId, UserMxgServlet userMxgServlet) {
		Users userListMsg = userMxgServlet.getUserListMsg(userId);
		this.userName = userListMsg.getUserName();
		this.userHeadPortraitUrl = userListMsg.getUserHeadPortraitUrl();
		return this;
	}
	
	public DataWithUserMsg(T data, int userId, UserMxgServlet u) {
		this.data = data;
		setUserMxg(userId, u);
	}
	
	public DataWithUserMsg() {
	}
	
	
}
