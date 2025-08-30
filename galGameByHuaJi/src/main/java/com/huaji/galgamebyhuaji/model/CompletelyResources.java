package com.huaji.galgamebyhuaji.model;

import com.huaji.galgamebyhuaji.entity.ResourceExtensionInformation;
import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.ResourcesJpegMap;
import com.huaji.galgamebyhuaji.entity.Tag;

import java.util.List;

public class CompletelyResources {
	private Resources resources;
	private ResourceExtensionInformation resourcesExtensionInformation;
	private List<ResourcesJpegMap> resourcesJpegList;
	private List<Tag> tagList;

	public CompletelyResources() {
	}

	public Resources getResources() {
		return resources;
	}

	public void setResources(Resources resources) {
		this.resources = resources;
	}

	public ResourceExtensionInformation getResourcesExtensionInformation() {
		return resourcesExtensionInformation;
	}

	public void setResourcesExtensionInformation(ResourceExtensionInformation resourcesExtensionInformation) {
		this.resourcesExtensionInformation = resourcesExtensionInformation;
	}

	public List<ResourcesJpegMap> getResourcesJpegList() {
		return resourcesJpegList;
	}

	public void setResourcesJpegList(List<ResourcesJpegMap> resourcesJpegList) {
		this.resourcesJpegList = resourcesJpegList;
	}

	public List<Tag> getTagList() {
		return tagList;
	}

	public void setTagList(List<Tag> tagList) {
		this.tagList = tagList;
	}
}