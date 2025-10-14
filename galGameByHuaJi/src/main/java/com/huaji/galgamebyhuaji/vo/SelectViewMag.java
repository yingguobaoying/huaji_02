package com.huaji.galgamebyhuaji.vo;


import com.huaji.galgamebyhuaji.entity.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author 滑稽/因果报应
 */
@Getter
@Setter
public class SelectViewMag {
	private Map<Long, Tag> tag;
	private int totalResources;
	private Map<String, Long> resourceStatistics;
}
