package com.huaji.galgamebyhuaji.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 滑稽/因果报应
 */
@Getter
@Setter
public class SelectMxg {
	private String rType;
	private List<Integer> tags;
	private int size;
	private String rName;
}
