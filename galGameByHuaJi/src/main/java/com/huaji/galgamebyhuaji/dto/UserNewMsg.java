package com.huaji.galgamebyhuaji.dto;

import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import com.huaji.galgamebyhuaji.annotation.CustomPE;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class UserNewMsg {
	@NotNull(message = "错误!不存在的用户!")
	private Integer userId;
	
	public void encryptionPassword () {
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
	
	public Integer getUserId () {
		return userId;
	}
	
	public void setUserId (Integer userId) {
		this.userId = userId;
	}
	
	public String getUserName () {
		return userName;
	}
	
	public void setUserName (String userName) {
		this.userName = userName == null ? null : userName.trim();
	}
	
	public String getUserNameLogin () {
		return userNameLogin;
	}
	
	public void setUserNameLogin (String userNameLogin) {
		this.userNameLogin = userNameLogin == null ? null : userNameLogin.trim();
	}
	
	public String getUserPassword () {
		return userPassword;
	}
	
	public void setUserPassword (String userPassword) {
		this.userPassword = userPassword == null ? null : userPassword.trim();
	}
	
	public Integer getCoin () {
		return coin;
	}
	
	public void setCoin (Integer coin) {
		this.coin = coin;
	}
	
	public String getMailbox () {
		return mailbox;
	}
	
	public void setMailbox (String mailbox) {
		this.mailbox = mailbox == null ? null : mailbox.trim();
	}
	
	public String getUserHeadPortraitUrl () {
		return userHeadPortraitUrl;
	}
	
	public void setUserHeadPortraitUrl (String userHeadPortraitUrl) {
		this.userHeadPortraitUrl = userHeadPortraitUrl == null ? null : userHeadPortraitUrl.trim();
	}
	
	public String getStatus () {
		return status;
	}
	
	public void setStatus (String status) {
		this.status = status == null ? null : status.trim();
	}
	
	public String getSex () {
		return sex;
	}
	
	public void setSex (String sex) {
		this.sex = sex == null ? null : sex.trim();
	}
	
	public Integer getJurisdiction () {
		return jurisdiction;
	}
	
	public void setJurisdiction (Integer jurisdiction) {
		this.jurisdiction = jurisdiction;
	}
	
	public Date getBirthday () {
		return birthday;
	}
	
	public void setBirthday (Date birthday) {
		this.birthday = birthday;
	}
	
	public Date getRegisterTime () {
		return registerTime;
	}
	
	public void setRegisterTime (Date registerTime) {
		this.registerTime = registerTime;
	}
	
	public String getUserPe () {
		return userPe;
	}
	
	public void setUserPe (String userPe) {
		this.userPe = userPe == null ? null : userPe.trim();
	}
	
	public String getBio () {
		return bio;
	}
	
	public void setBio (String bio) {
		this.bio = bio == null ? null : bio.trim();
	}
	
	public UsersWithBLOBs passUser () {
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