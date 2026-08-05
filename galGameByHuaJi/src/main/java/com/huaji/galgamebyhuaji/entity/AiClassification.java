package com.huaji.galgamebyhuaji.entity;

public class AiClassification {
    private Long id;

    private Long clientId;

    private Integer gaveUser;

    private Integer needJurisdiction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Integer getGaveUser() {
        return gaveUser;
    }

    public void setGaveUser(Integer gaveUser) {
        this.gaveUser = gaveUser;
    }

    public Integer getNeedJurisdiction() {
        return needJurisdiction;
    }

    public void setNeedJurisdiction(Integer needJurisdiction) {
        this.needJurisdiction = needJurisdiction;
    }
}