package com.huaji.galgamebyhuaji.constant;


import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;

import java.util.Date;

public class Constant {
	/**
	 * 全局游客信息,此处使用的是伪单例
	 */
	public final static Users TOURIST = new Tourist();
	/**
	 * 资源保存位置
	 */
	public static String RESOURCE_SAVE_PATH = "";
	/**
	 * 单次签到给的积分
	 */
	public static final int CHECK_IN_SCORE = 1;
	/**
	 * 外部连接默认价格
	 */
	public static final int EXTERNAL_CLOUD_DISK = 1;
	/**
	 * 服务器下载连接默认价格
	 */
	public static final int DOWNLOAD_LOCALLY = 10;
	/**
	 * 用来防止时序攻击的恒定比较密码
	 */
	public static String CONSTANT_PASSWORD;
	/**
	 * 初始硬币
	 */
	public static final int INIT_COIN = 5;
	/**
	 * 默认头像
	 */
	public static final String DEFAULT_HEAD_PORTRAIT = "default.jpeg";
	/**
	 * 允许上传的图片最小值
	 */
	public static final int JPEG_MIN = 1024;//图片最小值
	/**
	 * 允许图片上传的最大值
	 */
	public static final int JPEG_MAX = 10 * 1024 * 1024;//图片最大值
	/**
	 * 允许压缩包上传的最小值
	 */
	public static final int RAT_MIN = 1024 * 1024;//压缩包最小值
	/**
	 * 允许压缩包上传的最大值
	 */
	public static final long RAR_MAX = 20L * 1024 * 1024 * 1024;//压缩包最大值
	/**
	 * 资源购买后的有效时间,单位为小时
	 */
	public static final int RESOURCE_EXPIRATION_TIME = 24;
	/**
	 * 验证邮件有效期,单位毫秒
	 */
	public static final long VERIFY_EMAIL_VALID_TIME = 1000*60*60*12;
}

class Tourist extends UsersWithBLOBs {
	@Override
	public void setUserPassword(String userPassword) {
	}
	
	@Override
	public void setBio(String bio) {
	}
	
	@Override
	public void setUserId(Integer userId) {
	}
	
	@Override
	public void setUserName(String userName) {
	}
	
	@Override
	public void setUserNameLogin(String userNameLogin) {
	}
	
	@Override
	public void setCoin(Integer coin) {
	}
	
	@Override
	public void setMailbox(String mailbox) {
	}
	
	@Override
	public void setUserHeadPortraitUrl(String userHeadPortraitUrl) {
	}
	
	@Override
	public void setStatus(String status) {
	}
	
	@Override
	public void setSex(String sex) {
	}
	
	@Override
	public void setJurisdiction(Integer jurisdiction) {
	}
	
	@Override
	public void setBirthday(Date birthday) {
	}
	
	@Override
	public void setRegisterTime(Date registerTime) {
	}
	
	@Override
	public void setUserPe(String userPe) {
	}
	
	@Override
	public String getBio() {
		return null;
	}
	
	@Override
	public String getUserName() {
		return "游客用户";
	}
	
	@Override
	public String getUserNameLogin() {
		return "未登录的游客";
	}
	
	@Override
	public String getUserPassword() {
		return null;
	}
	
	@Override
	public Integer getUserId() {
		return -1;
	}
	
	@Override
	public Integer getCoin() {
		return 0;
	}
	
	@Override
	public String getMailbox() {
		return "未登录的游客";
	}
	
	@Override
	public String getUserHeadPortraitUrl() {
		return null;
	}
	
	@Override
	public String getStatus() {
		return "未登录的游客";
	}
	
	@Override
	public String getSex() {
		return "未知";
	}
	
	@Override
	public Date getBirthday() {
		return new Date(0);
	}
	
	@Override
	public Date getRegisterTime() {
		return null;
	}
	
	@Override
	public String getUserPe() {
		return null;
	}
	
	@Override
	public Integer getJurisdiction() {
		return JurisdictionLevel.TOURIST_JURISDICTION.getLevel();
	}
	

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return switch (o) {
			case Tourist tourist -> true;
			case Users users -> getUserId().equals(users.getUserId());
			case null, default -> false;
		};
	}
}
