package com.huaji.galgamebyhuaji.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuyLinkRequest {
	@JsonProperty("resourceId")
	private int resourceId;
	@JsonProperty("isLinks")
	private Boolean isLinks;
	@JsonProperty("isDownload")
	private Boolean isDownload;
	
	public int getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
	
	public Boolean getLinks() {
		return isLinks;
	}
	
	public void setLinks(Boolean links) {
		isLinks = links;
	}
	
	public Boolean getDownload() {
		return isDownload;
	}
	
	public void setDownload(Boolean download) {
		isDownload = download;
	}
}
