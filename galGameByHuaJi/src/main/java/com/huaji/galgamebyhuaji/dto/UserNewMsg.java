package com.huaji.galgamebyhuaji.dto;

import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import com.huaji.galgamebyhuaji.annotation.CustomPE;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserNewMsg {
	@NotNull(message = "错误!不存在的用户!")
	private Integer userId;
	
	public void encryptionPassword() {
		this.setUserPassword(MyStringUtil.encryption(userPassword));
	}
	
	@CustomNotNull(message = "用户名不可为空!")
	private String userName;
	
	@CustomNotNull(message = "用户登录名称不可为空")
	private String userNameLogin;
	
	@CustomNotNull(message = "用户密码不可为空!")
	@Size(min = 5, message = "用户密码至少为5位")
	private String userPassword;
	
	private Integer coin;
	
	@CustomNotNull(message = "用户邮箱信息不可为空!")
	@Email(message = "邮箱格式不正确")
	private String mailbox;
	
	private String userHeadPortraitUrl;
	
	private String status;
	
	private String sex;
	
	private Integer jurisdiction;
	
	private Date birthday;
	
	private Date registerTime;
	@CustomPE(message = "用户手机号格式错误!")
	private String userPe;
	
	private String bio;
	
	public UsersWithBLOBs passUser() {
		UsersWithBLOBs usersWithBLOBs = new UsersWithBLOBs();
		usersWithBLOBs.setUserName(userName);
		usersWithBLOBs.setUserNameLogin(userNameLogin);
		usersWithBLOBs.setUserPassword(userPassword);
		usersWithBLOBs.setCoin(coin);
		usersWithBLOBs.setMailbox(mailbox);
		usersWithBLOBs.setUserHeadPortraitUrl(userHeadPortraitUrl);
		usersWithBLOBs.setStatus(status);
		usersWithBLOBs.setSex(sex);
		usersWithBLOBs.setJurisdiction(jurisdiction);
		usersWithBLOBs.setBirthday(birthday);
		usersWithBLOBs.setRegisterTime(registerTime);
		usersWithBLOBs.setUserPe(userPe);
		usersWithBLOBs.setBio(bio);
		return usersWithBLOBs;
	}
}
