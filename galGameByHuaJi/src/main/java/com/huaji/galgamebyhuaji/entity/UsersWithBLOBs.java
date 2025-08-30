package com.huaji.galgamebyhuaji.entity;

public class UsersWithBLOBs extends Users {
    private String userPassword;

    private String bio;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword == null ? null : userPassword.trim();
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio == null ? null : bio.trim();
    }
}