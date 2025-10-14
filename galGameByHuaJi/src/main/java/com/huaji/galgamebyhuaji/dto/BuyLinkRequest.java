package com.huaji.galgamebyhuaji.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyLinkRequest {
	
	@JsonProperty("resourceId")
	private int resourceId;
	@JsonProperty("isLinks")
	private Boolean isLinks;
	@JsonProperty("isDownload")
	private Boolean isDownload;
}
