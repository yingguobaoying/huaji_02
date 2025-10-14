package com.huaji.galgamebyhuaji.entity;

public class ResourceExtensionInformation {
    private Integer rId;

    private Integer linkPrice;

    private Integer downloadLocallyPrice;

    private String hasDownloadLocally;
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ResourceExtensionInformation{");
        sb.append("rId=").append(rId);
        sb.append(", linkPrice=").append(linkPrice);
        sb.append(", downloadLocallyPrice=").append(downloadLocallyPrice);
        sb.append(", hasDownloadLocally='").append(hasDownloadLocally).append('\'');
        sb.append('}');
        return sb.toString();
    }
    
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
