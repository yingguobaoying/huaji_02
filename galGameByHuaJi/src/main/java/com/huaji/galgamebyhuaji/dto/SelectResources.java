package com.huaji.galgamebyhuaji.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class SelectResources {
	private List<Integer> tagList;
	private int tagSize;
	private String name;
	private String type;
	private String manufacturer;
	private int size;
	private int page;
	
	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	
	public List<Integer> getTagList() {
		return tagList;
	}
	
	public void setTagList(List<Integer> tagList) {
		this.tagList = tagList;
	}
	
	public int getTagSize() {
		return tagSize;
	}
	
	public void setTagSize(int tagSize) {
		this.tagSize = tagSize;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getManufacturer() {
		return manufacturer;
	}
	
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
}
