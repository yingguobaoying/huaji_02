package com.huaji.galgamebyhuaji.controller;


import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.FileUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.RedisMemoryService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {
	final
	ResourcesService resourcesService;
	final
	RedisMemoryService redisMemoryService;
	
	
	@GetMapping("/getResources/{star}/{end}")
	public ReturnResult<Resources> getResourcesList(@PathVariable("star") int star, @PathVariable("end") int end) {
		int c = star + end;
		end = Integer.max(star, end);
		star = Integer.max(c - end, 0);
		List<Resources> resourceList = resourcesService.getResourceList(star, end);
		if (resourceList.isEmpty()) {
			throw new OperationException("资源获取失败,可能是数据库或者服务器出现了问题!如果服务器状态正常的话，请联系管理员!");
		}
		return ReturnResult.isTrue("资源获取成功", resourceList, resourcesService.getResourceListSize());
	}
	
	
	@GetMapping("/getResources/{rId}")
	public ReturnResult<Resources> getResources(@PathVariable("rId") int rId) {
		Resources resource = resourcesService.getResource(rId);
		if (!MyStringUtil.isNull(resource.getrJpeg()))
			FileUtil.toRelativeUrl(resource.getrJpeg(), FileCategory.IMG);
		// 处理图片列表URL
		if (resource.getrPicture() != null && !resource.getrPicture().isEmpty()) {
			List<String> processedPictures = resource.getrPicture().stream()
					.map(rj -> MyStringUtil.isNull(rj) ? "null" : FileUtil.toRelativeUrl(rj, FileCategory.IMG))
					.collect(Collectors.toList());
			resource.setrPicture(processedPictures);
		}
		return ReturnResult.isTrue("资源获取成功", resource);
	}
}
