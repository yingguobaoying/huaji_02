package com.huaji.galgamebyhuaji.dto;


import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class UserMxgWithOldUserMxg {
	@Valid
	private UserNewMsg users;
	@CustomNotNull(message = "旧密码不可为空!")
	@Size(min = 5, message = "旧密码错误!")
	private String oldPassWord;
	@CustomNotNull(message = "旧邮箱信息不可为空")
	private String oldMailbox;
	
	public boolean isNeedUserHead () {
		return needUserHead;
	}
	
	public void setNeedUserHead (boolean needUserHead) {
		this.needUserHead = needUserHead;
	}
	
	private boolean needUserHead;
	
	public @Valid UserNewMsg getUsers () {
		return users;
	}
	
	public void setUsers (@Valid UserNewMsg users) {
		this.users = users;
	}
	
	public String getOldPassWord () {
		return oldPassWord;
	}
	
	public void setOldPassWord (String oldPassWord) {
		this.oldPassWord = oldPassWord;
	}
	
	public String getOldMailbox () {
		return oldMailbox;
	}
	
	public void setOldMailbox (String oldMailbox) {
		this.oldMailbox = oldMailbox;
	}
}