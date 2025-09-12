package com.huaji.galgamebyhuaji.dto;

import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.model.jwtToken.BestEntityToken;
import com.huaji.galgamebyhuaji.model.jwtToken.BuyResourcesUser;

public class BuyResourcesUserDTO {
	private int resourceId;
	private boolean isLinks;
	private boolean isDownload;
	
	public BuyResourcesUserDTO() {
	}
	
	public BuyResourcesUserDTO(BuyResourcesUser user) {
		this.resourceId = user.getResourceId();
		this.isLinks = user.isLinks();
		this.isDownload = user.isDownload();
	}
	
	public int getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
	
	public boolean isLinks() {
		return isLinks;
	}
	
	public void setLinks(boolean links) {
		isLinks = links;
	}
	
	public boolean isDownload() {
		return isDownload;
	}
	
	public void setDownload(boolean download) {
		isDownload = download;
	}
}
