package com.huaji.galgamebyhuaji.entity;

public class FriendMap extends FriendMapKey {
    private Boolean friendConfirmation;

    private Integer initiateUser;

    public Boolean getFriendConfirmation() {
        return friendConfirmation;
    }

    public void setFriendConfirmation(Boolean friendConfirmation) {
        this.friendConfirmation = friendConfirmation;
    }

    public Integer getInitiateUser() {
        return initiateUser;
    }

    public void setInitiateUser(Integer initiateUser) {
        this.initiateUser = initiateUser;
    }
}