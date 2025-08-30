package com.huaji.galgamebyhuaji.entity;

import java.util.Date;

public class UserResourceRepository {
    private Integer userId;

    private Integer rId;

    private String getType;

    private Date expirationTime;

    private Date getTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getrId() {
        return rId;
    }

    public void setrId(Integer rId) {
        this.rId = rId;
    }

    public String getGetType() {
        return getType;
    }

    public void setGetType(String getType) {
        this.getType = getType == null ? null : getType.trim();
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }
}