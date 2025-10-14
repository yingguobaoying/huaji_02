package com.huaji.galgamebyhuaji.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectResources {
	private List<Integer> tagList;
	private int tagSize;
	private String name;
	private String type;
	private String manufacturer;
	private int size;
	private int page;
}
