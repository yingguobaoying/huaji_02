package com.huaji.galgamebyhuaji.dto;


import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import jakarta.validation.constraints.Size;

/**
 * 这个是登录用户的信息打包
 *
 * @author 滑稽/因果报应
 */
public class LoginUserMxg {


	@CustomNotNull(message = "用户名不可为空!")
	private String userName;

	@CustomNotNull(message = "用户密码不可为空!")
	@Size(min = 5, message = "用户密码至少为5位")
	private String userPassword;

	private Boolean unitIsDay;

	private Integer keepTime;


	private LoginUserMxg() {
	}


	public Integer getKeepTime() {
		return keepTime;
	}

	public void setKeepTime(Integer keepTime) {
		this.keepTime = keepTime;
	}

	public Boolean getUnitIsDay() {
		return unitIsDay;
	}

	public void setUnitIsDay(Boolean unitIsDay) {
		this.unitIsDay = unitIsDay;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}


	public UsersWithBLOBs getUsers() {
		UsersWithBLOBs users = new UsersWithBLOBs();
		users.setUserNameLogin(userName);
		users.setUserPassword(userPassword);
		users.setMailbox(userName);
		users.setUserPe(userName);
		return users;
	}
}