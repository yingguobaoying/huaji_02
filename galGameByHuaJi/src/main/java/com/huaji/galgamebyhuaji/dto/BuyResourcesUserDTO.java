package com.huaji.galgamebyhuaji.dto;

import com.huaji.galgamebyhuaji.model.jwtToken.BuyResourcesUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	
	
}
