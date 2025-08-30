package com.huaji.galgamebyhuaji.entity;

public class ResourcesFileMap {
    private Integer rId;

    private String fileName;

    private Integer upUser;

    private Boolean isPublic;

    private Integer size;

    public Integer getrId() {
        return rId;
    }

    public void setrId(Integer rId) {
        this.rId = rId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Integer getUpUser() {
        return upUser;
    }

    public void setUpUser(Integer upUser) {
        this.upUser = upUser;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    
}