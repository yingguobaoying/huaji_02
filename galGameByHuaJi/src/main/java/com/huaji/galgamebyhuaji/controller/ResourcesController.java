package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.dto.DTOResources;
import com.huaji.galgamebyhuaji.dto.FileUp;
import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.service.ResourcesFileService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 由于此类之后应该是只有我在用所以这里返回值化简,也不管架构如何了,怎么简单怎么来 XD
 */
@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
@ResponseBody
public class ResourcesController extends BaseController {
	final ResourcesService resourcesService;
	final ResourcesFileService resourcesFileService;
	
	@PostMapping("/resources/add")
	@PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
	public ReturnResult<String> addResources(@Valid @RequestBody DTOResources resources, BindingResult testResult) {
		if (testResult.hasErrors())
			if (testResult.getFieldError() != null)
				return ReturnResult.isFalse(testResult.getFieldError().getField());
			else
				return ReturnResult.isFalse("未知错误，请稍后重试。");
		Users loginUser = getLoginUser();
		Resources resources1 = resources.getResourcesMsg();
		MyLogUtil.info(ResourcesController.class, "管理员%d{%s}开始添加资源信息:{%s},tagId信息:{%s}".formatted(loginUser.getUserId(), loginUser.getUserName(), resources1, resources.getTags()));
		resourcesService.addResources(resources1, resources.getTags());
		MyLogUtil.info(ResourcesController.class, "管理员%d{%s}添加资源信息:{%s},tagId信息:{%s}".formatted(loginUser.getUserId(), loginUser.getUserName(), resources1, resources1.getTags()));
		return ReturnResult.isTrue("资源添加成功!", "资源添加成功");
	}
	
	@PostMapping("/resources/update")
	@PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
	public ReturnResult<String> updateResources(@Valid @RequestBody DTOResources resources, BindingResult testResult) {
		if (testResult.hasErrors())
			if (testResult.getFieldError() != null)
				return ReturnResult.isFalse(testResult.getFieldError().getField());
			else
				return ReturnResult.isFalse("未知错误，请稍后重试。");
		Users loginUser = getLoginUser();
		Resources resources1 = resources.getResourcesMsg();
		MyLogUtil.info(ResourcesController.class, "管理员%d{%s}尝试修改添加资源信息".formatted(loginUser.getUserId(), loginUser.getUserName()));
		resourcesService.updateResources(resources1, resources.getTags());
		MyLogUtil.info(ResourcesController.class, "管理员%d{%s}修改资源信息:{%s},tagId信息:{%s}".formatted(loginUser.getUserId(), loginUser.getUserName(), resources1, resources1.getTags()));
		return ReturnResult.isTrue("资源添加成功!", "资源添加成功");
	}
	
	@GetMapping("/resources/delete/{id}")
	@PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
	public ReturnResult<String> delResources(@PathVariable int id) {
		Users loginUser = getLoginUser();
		Resources resource = resourcesService.getResource(id);
		if (resource == null || resource.getrId() == null || id < 0)
			return ReturnResult.isFalse("资源不存在");
		MyLogUtil.info(ResourcesController.class, "管理员%d{%s}尝试删除资源%d{%s}信息".formatted(loginUser.getUserId(), loginUser.getUserName(), resource.getrId(), resource.getrName()));
		ReturnResult<Resources> resourcesReturnResult = resourcesService.deleteResources(id);
		MyLogUtil.info(ResourcesController.class, "管理员%d{%s}删除了资源%d{%s}信息".formatted(loginUser.getUserId(), loginUser.getUserName(), resource.getrId(), resource.toString()));
		return null;
	}
	
	@PostMapping("/resources/addFile/img")
	@PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
	public ReturnResult<String> addFileResourcesImg(@ModelAttribute FileUp fileUpMsg) throws IOException {
		if (fileUpMsg.getFileList() == null || fileUpMsg.getFileList().isEmpty() || fileUpMsg.getFileSize() <= 0)
			return ReturnResult.isFalse("上传至少一个文件!");
		List<MultipartFile> fileList = fileUpMsg.getFileList();
		if (fileList.size() != fileUpMsg.getFileSize())
			return ReturnResult.isFalse("错误!文件数量校验错误,期望接收%d个,实际为:%d个".formatted(fileUpMsg.getFileSize(), fileUpMsg.getFileList().size()));
		if (fileUpMsg.isHasFirst()) {
			if (fileList.size() != 1) {//将首尾文件交换,方便接下来操作
				Collections.swap(fileList, 0, fileList.size() - 1);
			}
		}
		return ReturnResult.isTrue(resourcesFileService.addResourceImg(fileUpMsg.getFileList(), fileUpMsg.getAtResource(), fileUpMsg.isHasFirst()), "操作成功");
	}
	
	@PostMapping("/resources/addFile/rar")
	@PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
	public ReturnResult<String> addFileResourcesRar() {
		return null;
	}
	
	@PostMapping("resources/delFile")
	@PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
	public ReturnResult<String> delFileResources() {
		return null;
	}
}
