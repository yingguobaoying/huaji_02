package com.huaji.galgamebyhuaji.entity;

public class ResourcesJpegMap {
    private Integer resourcesId;

    private String jpegName;

    public Integer getResourcesId() {
        return resourcesId;
    }

    public void setResourcesId(Integer resourcesId) {
        this.resourcesId = resourcesId;
    }

    public String getJpegName() {
        return jpegName;
    }

    public void setJpegName(String jpegName) {
        this.jpegName = jpegName == null ? null : jpegName.trim();
    }
}