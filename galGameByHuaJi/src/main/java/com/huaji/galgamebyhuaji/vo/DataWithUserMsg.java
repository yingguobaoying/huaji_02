package com.huaji.galgamebyhuaji.vo;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;

/**
 * @author 滑稽/因果报应
 */
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
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserHeadPortraitUrl() {
		return userHeadPortraitUrl;
	}
	
	public void setUserHeadPortraitUrl(String userHeadPortraitUrl) {
		this.userHeadPortraitUrl = userHeadPortraitUrl;
	}
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
}
