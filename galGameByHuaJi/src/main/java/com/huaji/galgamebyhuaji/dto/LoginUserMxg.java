package com.huaji.galgamebyhuaji.dto;


import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 这个是登录用户的信息打包
 *
 * @author 滑稽/因果报应
 */
@Getter
@Setter
@EqualsAndHashCode
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
	
	
	public UsersWithBLOBs getUsers() {
		UsersWithBLOBs users = new UsersWithBLOBs();
		users.setUserNameLogin(userName);
		users.setUserPassword(userPassword);
		users.setMailbox(userName);
		users.setUserPe(userName);
		return users;
	}
}
