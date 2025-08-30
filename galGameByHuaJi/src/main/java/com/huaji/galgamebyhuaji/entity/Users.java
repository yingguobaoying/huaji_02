package com.huaji.galgamebyhuaji.entity;

import java.util.Date;

public class Users {
    public Users(UsersWithBLOBs u) {
        this.userId = u.getUserId();
        this.userName = u.getUserName();
        this.userNameLogin = u.getUserNameLogin();
        this.coin = u.getCoin();
        this.mailbox = u.getMailbox();
        this.userHeadPortraitUrl = u.getUserHeadPortraitUrl();
        this.status = u.getStatus();
        this.sex = u.getSex();
        this.jurisdiction = u.getJurisdiction();
        this.birthday = u.getBirthday();
        this.registerTime = u.getRegisterTime();
        this.userPe = u.getUserPe();
    }

    public Users() {
    }

    private Integer userId;

    private String userName;

    private String userNameLogin;

    private Integer coin;

    private String mailbox;

    private String userHeadPortraitUrl;

    private String status;

    private String sex;

    private Integer jurisdiction;

    private Date birthday;

    private Date registerTime;

    private String userPe;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserNameLogin() {
        return userNameLogin;
    }

    public void setUserNameLogin(String userNameLogin) {
        this.userNameLogin = userNameLogin == null ? null : userNameLogin.trim();
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox == null ? null : mailbox.trim();
    }

    public String getUserHeadPortraitUrl() {
        return userHeadPortraitUrl;
    }

    public void setUserHeadPortraitUrl(String userHeadPortraitUrl) {
        this.userHeadPortraitUrl = userHeadPortraitUrl == null ? null : userHeadPortraitUrl.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public Integer getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Integer jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getUserPe() {
        return userPe;
    }

    public void setUserPe(String userPe) {
        this.userPe = userPe == null ? null : userPe.trim();
    }
}