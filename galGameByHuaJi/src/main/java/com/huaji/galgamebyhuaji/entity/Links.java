package com.huaji.galgamebyhuaji.entity;

import java.util.Date;

public class Links {
    private Long linkId;

    private Integer linkUpUser;

    private Integer linkR;

    private String linkState;

    private String linksPublic;

    private Date upTime;

    private String linkPointer;

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Integer getLinkUpUser() {
        return linkUpUser;
    }

    public void setLinkUpUser(Integer linkUpUser) {
        this.linkUpUser = linkUpUser;
    }

    public Integer getLinkR() {
        return linkR;
    }

    public void setLinkR(Integer linkR) {
        this.linkR = linkR;
    }

    public String getLinkState() {
        return linkState;
    }

    public void setLinkState(String linkState) {
        this.linkState = linkState == null ? null : linkState.trim();
    }

    public String getLinksPublic() {
        return linksPublic;
    }

    public void setLinksPublic(String linksPublic) {
        this.linksPublic = linksPublic == null ? null : linksPublic.trim();
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }

    public String getLinkPointer() {
        return linkPointer;
    }

    public void setLinkPointer(String linkPointer) {
        this.linkPointer = linkPointer == null ? null : linkPointer.trim();
    }
}