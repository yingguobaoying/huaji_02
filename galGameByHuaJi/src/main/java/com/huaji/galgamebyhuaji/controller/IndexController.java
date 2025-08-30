package com.huaji.galgamebyhuaji.controller;


import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.service.RedisMemoryService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/index")
public class IndexController {
	@Autowired
	ResourcesService resourcesService;
	@Autowired
	RedisMemoryService redisMemoryService;

	@GetMapping("/getResources/{star}/{end}")
	public ReturnResult<Resources> getResourcesList(@PathVariable("star") int star, @PathVariable("end") int end) {
		int c = star + end;
		end = Integer.max(star, end);
		star = Integer.max(c - end, 0);
		System.err.println("****************************test**************************************************");
		List<Resources> resourceList = resourcesService.getResourceList(star, end);
		if (resourceList.isEmpty()) {
			throw new OperationException("资源获取失败,可能是数据库或者服务器出现了问题!如果服务器状态正常的话，请联系管理员!");
		}
		return ReturnResult.isTrue("资源获取成功", resourceList, resourcesService.getResourceListSize());
	}


	@GetMapping("/getResources/{rId}")
	public ReturnResult<Resources> getResources(@PathVariable("rId") int rId) {
		return ReturnResult.isTrue("资源获取成功", resourcesService.getResource(rId));
	}

	@PostConstruct
	public void init() {
		System.out.println("IndexController 加载成功");
	}
}