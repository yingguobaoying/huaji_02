package com.huaji.galgamebyhuaji.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import com.huaji.galgamebyhuaji.entity.ResourceExtensionInformation;
import com.huaji.galgamebyhuaji.entity.Resources;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DTOResources {
	@JsonProperty("rId")
	private Integer rId;
	
	@CustomNotNull(message = "资源名称不可为空")
	@JsonProperty("rName")
	private String rName;
	
	@CustomNotNull(message = "资源发行商/厂商名称不可为空")
	@JsonProperty("rManufacturer")
	private String rManufacturer;
	
	@CustomNotNull(message = "资源类型不可为空")
	@JsonProperty("rType")
	private String rType;
	
	@CustomNotNull(message = "资源简介不可为空")
	@JsonProperty("rIntroduction")
	private String rIntroduction;
	
	@JsonProperty("tags")
	private List<Integer> tags = new ArrayList<>(8);
	
	@Min(message = "外部资源价格范围:1-100", value = 1)
	@Max(message = "外部资源价格范围:1-100", value = 100)
	@JsonProperty("linkPrice")
	private Integer linkPrice;
	@Min(message = "本地资源下载价格范围:5-100", value = 5)
	@Max(message = "本地资源下载价格范围:5-100", value = 100)
	@JsonProperty("downloadLocallyPrice")
	private Integer downloadLocallyPrice;
	
	public Resources getResourcesMsg() {
		Resources resources = new Resources();
		resources.setrId(this.getRId());
		resources.setrName(this.getRName());
		resources.setrManufacturer(this.getRManufacturer());
		resources.setrType(this.getRType());
		resources.setrIntroduction(this.getRIntroduction());
		ResourceExtensionInformation resourceExtensionInformation1 = new ResourceExtensionInformation();
		resourceExtensionInformation1.setLinkPrice(this.linkPrice);
		resourceExtensionInformation1.setDownloadLocallyPrice(this.downloadLocallyPrice);
		resources.setResourceExtensionInformation(resourceExtensionInformation1);
		return resources;
	}
}
