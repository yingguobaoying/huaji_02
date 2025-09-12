package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.entity.LinksWithBLOBs;
import com.huaji.galgamebyhuaji.entity.ResourcesFileMap;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.model.jwtToken.BuyResourcesUser;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.FileServlet;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.vo.DataWithUserMsg;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.management.openmbean.OpenDataException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.huaji.galgamebyhuaji.myUtil.ElseUtil.getToken;

@Controller
@RequestMapping("api/user/download")
public class DownloadFileController extends BaseController {
	final
	FileServlet fileServlet;
	final
	TokenService tokenService;
	final
	ResourcesService resourcesService;
	
	public DownloadFileController(FileServlet fileServlet, TokenService tokenService, ResourcesService resourcesService) {
		this.fileServlet = fileServlet;
		this.tokenService = tokenService;
		this.resourcesService = resourcesService;
	}
	
	@GetMapping("/rar")
	public ResponseEntity<InputStreamResource> downloadRar(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam("fileName") String fileName,
			@RequestParam(value = "userNaming", required = false) String userNaming
	) throws BestException, IOException {
		if (MyStringUtil.isNull(userNaming))
			userNaming = fileName;
		Users loginUser = getLoginUser(true);
		String token = getToken(request, SystemConstant.USER_BUY_TOKEN);
		OnlineUser onlineUser = tokenService.VerifyAndParse(token, loginUser.getUserId(), TokenType.GET_DOWNLOAD, ElseUtil.getClientIp(request));
		if (onlineUser instanceof BuyResourcesUser user) {
			if (user.isDownload()) {
				int rId = user.getResourceId();
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
				ReturnResult<ResponseEntity<InputStreamResource>> r = fileServlet.dowFile(fileName, userNaming, getToken(request), rId,"rar");
				if (r.isOperationResult()) {
					return r.getReturnResult();
				} else {
					throw new OperationException(r.getMxg());
				}
				
			} else {
				throw new OperationException("您还没有购买本地下载权限");
			}
		} else throw new OperationException("出错了,请检查您提供的令牌,令牌类型不符");
	}
}
