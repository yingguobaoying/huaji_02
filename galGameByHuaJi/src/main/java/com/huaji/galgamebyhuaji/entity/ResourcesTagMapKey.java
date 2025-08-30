package com.huaji.galgamebyhuaji.entity;

public class ResourcesTagMapKey {
    private Integer rId;

    private Integer tagId;

    public ResourcesTagMapKey(Integer rId, Integer tagId) {
        this.rId = rId;
        this.tagId = tagId;
    }

    public ResourcesTagMapKey() {
    }

    public Integer getrId() {
        return rId;
    }

    public void setrId(Integer rId) {
        this.rId = rId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}