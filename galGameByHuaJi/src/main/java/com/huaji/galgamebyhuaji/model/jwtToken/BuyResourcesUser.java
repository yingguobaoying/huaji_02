package com.huaji.galgamebyhuaji.model.jwtToken;

import com.huaji.galgamebyhuaji.enumPackage.TokenType;

public class BuyResourcesUser extends BestEntityToken {
	private int resourceId;
	private boolean isLinks;
	private boolean isDownload;
	
	public BuyResourcesUser () {
		super(TokenType.GET_DOWNLOAD);
	}
	
	
	public int getResourceId () {
		return resourceId;
	}
	
	public void setResourceId (int resourceId) {
		this.resourceId = resourceId;
	}
	
	public boolean isLinks () {
		return isLinks;
	}
	
	public void setLinks (boolean links) {
		isLinks = links;
	}
	
	public boolean isDownload () {
		return isDownload;
	}
	
	public void setDownload (boolean download) {
		isDownload = download;
	}
}