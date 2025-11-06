package com.huaji.galgamebyhuaji.entity;

import java.util.Date;

public class UserResourceRepository {
    private Integer userId;

    private Integer rId;

    private Boolean hasDown;

    private Date expirationTime;

    private Date getTime;

    private Boolean hasLink;

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

    public Boolean getHasDown() {
        return hasDown;
    }

    public void setHasDown(Boolean hasDown) {
        this.hasDown = hasDown;
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

    public Boolean getHasLink() {
        return hasLink;
    }

    public void setHasLink(Boolean hasLink) {
        this.hasLink = hasLink;
    }
}