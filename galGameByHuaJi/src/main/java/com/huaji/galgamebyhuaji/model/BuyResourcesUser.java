package com.huaji.galgamebyhuaji.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyResourcesUser {
	private int resourceId;
	private boolean isLinks;
	private boolean isDownload;
	private int userId;
}
