package com.huaji.galgamebyhuaji.dto;

import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import com.huaji.galgamebyhuaji.entity.ResourceExtensionInformation;
import com.huaji.galgamebyhuaji.entity.Resources;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DTOResources {
	private Integer rId;
	@CustomNotNull(message = "资源名称不可为空")
	private String rName;
	@CustomNotNull(message = "资源发行商/厂商名称不可为空")
	private String rManufacturer;
	@CustomNotNull(message = "资源类型不可为空")
	private String rType;
	@CustomNotNull(message = "资源简介不可为空")
	private String rIntroduction;
	
	private List<Integer> tags = new ArrayList<>(8);
	
	@Size(message = "外部资源价格范围:1-100", min = 1, max = 100)
	
	private Integer linkPrice;
	@Size(message = "本地资源下载价格范围:5-100", min = 5, max = 100)
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
