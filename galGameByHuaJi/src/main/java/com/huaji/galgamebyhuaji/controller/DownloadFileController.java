package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.entity.ResourcesFileMap;
import com.huaji.galgamebyhuaji.entity.UserResourceRepository;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.FileAccessService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/user/download")
@RequiredArgsConstructor
public class DownloadFileController extends BaseController {
	final FileAccessService fileAccessService;
	final TokenService tokenService;
	final ResourcesService resourcesService;
	final UserBehaviorService userBehaviorService;
	
	@GetMapping("/rar")
	public ResponseEntity<InputStreamResource> downloadRar(
			@RequestParam("fileName") String fileName,
			@RequestParam(value = "userNaming", required = false) String userNaming,
			@RequestParam(value = "rId") int rId
	) throws IOException {
		if (MyStringUtil.isNull(userNaming))
			userNaming = fileName;
		Users loginUser = getLoginUser();
		ReturnResult<UserResourceRepository> userResource = userBehaviorService.getUserResource(loginUser.getUserId(), rId);
		if (!(userResource.isOperationResult() && userResource.getReturnResult().getHasDown()))
			throw new OperationException("您还没有获取本地资源的下载权限呢~~");
		List<ResourcesFileMap> resourceFileList = resourcesService.getResourceFileList(rId);
		boolean hasFile = false;
		for (ResourcesFileMap map : resourceFileList) {
			if (map.getFileName().equals(fileName)) {
				hasFile = true;
				break;
			}
		}
		if (!hasFile)
			throw new OperationException("文件不存在");
		ReturnResult<ResponseEntity<InputStreamResource>> r = fileAccessService.downloadFile(fileName, FileCategory.ARCHIVE, fileName, loginUser.getUserId(), rId);
		if (r.isOperationResult()) {
			return r.getReturnResult();
		} else {
			throw new OperationException(r.getMsg());
		}
		
	}
}
