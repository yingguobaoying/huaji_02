package com.huaji.galgamebyhuaji.vo;


import com.huaji.galgamebyhuaji.entity.Tag;

import java.util.Map;

/**
 * @author 滑稽/因果报应
 */
public class SelectViewMag {
	private Map<Long, Tag> tag;
	private int totalResources;
	private Map<String, Long> resourceStatistics;

	public void setTag(Map<Long, Tag> tag) {
		this.tag = tag;
	}

	public void setTotalResources(int totalResources) {
		this.totalResources = totalResources;
	}

	public void setResourceStatistics(Map<String, Long> resourceStatistics) {
		this.resourceStatistics = resourceStatistics;
	}
}