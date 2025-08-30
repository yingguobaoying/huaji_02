package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Resources {
	private Integer rId;

	private String rName;

	private String rManufacturer;

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public void addTag(Tag t) {
		if (tags == null) {
			tags = new ArrayList<Tag>();
		}
		tags.add(t);
	}
	private String rType;

	private String rJpeg;

	private Date rEnterTime;

	private Long upUser;

	private String rIntroduction;

	private List<Tag> tags =new ArrayList<>(8);;
	private List<String> rPicture = new ArrayList<>(4);;
	private ResourceExtensionInformation resourceExtensionInformation;

	public ResourceExtensionInformation getResourceExtensionInformation() {
		return resourceExtensionInformation;
	}

	public void setResourceExtensionInformation(ResourceExtensionInformation resourceExtensionInformation) {
		this.resourceExtensionInformation = resourceExtensionInformation;
	}

	public void addRPicture(String rPicture) {
		if (this.rPicture == null) this.rPicture = new ArrayList<>();
		this.rPicture.add(rPicture);
	}

	public List<String> getrPicture() {
		return rPicture;
	}

	public void setrPicture(List<String> rPicture) {
		this.rPicture = rPicture;
	}

	public Resources(Resources resources) {
		rId = resources.rId;
		rName = resources.rName;
		rManufacturer = resources.rManufacturer;
		rType = resources.rType;
		rJpeg = resources.rJpeg;
		rEnterTime = resources.rEnterTime;
		upUser = resources.upUser;
		rIntroduction = resources.rIntroduction;
		tags = resources.tags;
		rPicture = resources.rPicture;
		resourceExtensionInformation = resources.resourceExtensionInformation;
	}

	public Resources() {
	}

	public Integer getrId() {
		return rId;
	}

	public void setrId(Integer rId) {
		this.rId = rId;
	}

	public String getrName() {
		return rName;
	}

	public void setrName(String rName) {
		this.rName = rName == null ? null : rName.trim();
	}

	public String getrManufacturer() {
		return rManufacturer;
	}

	public void setrManufacturer(String rManufacturer) {
		this.rManufacturer = rManufacturer == null ? null : rManufacturer.trim();
	}

	public String getrType() {
		return rType;
	}

	public void setrType(String rType) {
		this.rType = rType == null ? null : rType.trim();
	}

	public String getrJpeg() {
		return rJpeg;
	}

	public void setrJpeg(String rJpeg) {
		this.rJpeg = rJpeg == null ? null : rJpeg.trim();
	}

	public Date getrEnterTime() {
		return rEnterTime;
	}

	public void setrEnterTime(Date rEnterTime) {
		this.rEnterTime = rEnterTime;
	}

	public Long getUpUser() {
		return upUser;
	}

	public void setUpUser(Long upUser) {
		this.upUser = upUser;
	}

	public String getrIntroduction() {
		return rIntroduction;
	}

	public void setrIntroduction(String rIntroduction) {
		this.rIntroduction = rIntroduction == null ? null : rIntroduction.trim();
	}
}