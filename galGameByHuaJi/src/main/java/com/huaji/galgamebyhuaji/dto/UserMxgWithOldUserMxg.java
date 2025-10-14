package com.huaji.galgamebyhuaji.dto;


import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMxgWithOldUserMxg {
	@Valid
	private UserNewMsg users;
	@CustomNotNull(message = "旧密码不可为空!")
	@Size(min = 5, message = "旧密码错误!")
	private String oldPassWord;
	@CustomNotNull(message = "旧邮箱信息不可为空")
	private String oldMailbox;
	
	
	private boolean needUserHead;
	
	public @Valid UserNewMsg getUsers() {
		return users;
	}
	
	public void setUsers(@Valid UserNewMsg users) {
		this.users = users;
	}
}
