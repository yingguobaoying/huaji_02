package com.huaji.galgamebyhuaji.dto;


import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import jakarta.validation.constraints.NotNull;

/**
 * @author 滑稽/因果报应
 */
public class LinkMxg {
    @NotNull(message = "资源类型不可为空!")
    private Integer rId;

    @CustomNotNull(message = "用户名称不可为空!")
    private String userName;

    @CustomNotNull(message = "用户密码不可为空!")
    private String password;

    private Boolean needAll;

    public @NotNull(message = "资源类型不可为空!") Integer getrId() {
        return rId;
    }

    public void setrId(@NotNull(message = "资源类型不可为空!") Integer rId) {
        this.rId = rId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getNeedAll() {
        return needAll;
    }

    public void setNeedAll(Boolean needAll) {
        this.needAll = needAll;
    }
}