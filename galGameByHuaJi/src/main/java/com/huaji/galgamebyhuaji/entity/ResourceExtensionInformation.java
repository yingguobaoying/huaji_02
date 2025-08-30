package com.huaji.galgamebyhuaji.entity;

public class ResourceExtensionInformation {
    private Integer rId;

    private Integer linkPrice;

    private Integer downloadLocallyPrice;

    private String hasDownloadLocally;

    public Integer getrId() {
        return rId;
    }

    public void setrId(Integer rId) {
        this.rId = rId;
    }

    public Integer getLinkPrice() {
        return linkPrice;
    }

    public void setLinkPrice(Integer linkPrice) {
        this.linkPrice = linkPrice;
    }

    public Integer getDownloadLocallyPrice() {
        return downloadLocallyPrice;
    }

    public void setDownloadLocallyPrice(Integer downloadLocallyPrice) {
        this.downloadLocallyPrice = downloadLocallyPrice;
    }

    public String getHasDownloadLocally() {
        return hasDownloadLocally;
    }

    public void setHasDownloadLocally(String hasDownloadLocally) {
        this.hasDownloadLocally = hasDownloadLocally == null ? null : hasDownloadLocally.trim();
    }
}