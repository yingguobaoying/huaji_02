package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dto.BuyLinkRequest;
import com.huaji.galgamebyhuaji.dto.BuyResourcesUserDTO;
import com.huaji.galgamebyhuaji.entity.LinksWithBLOBs;
import com.huaji.galgamebyhuaji.entity.ResourcesFileMap;
import com.huaji.galgamebyhuaji.entity.UserResourceRepository;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.JWTUtil;
import com.huaji.galgamebyhuaji.service.LinkService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserBehaviorService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import com.huaji.galgamebyhuaji.vo.DataWithUserMsg;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class BuyResourcesController extends BaseController {
	final
	ResourcesService resourcesService;
	final
	TokenService tokenService;
	final
	JWTUtil jwtUtil;
	final
	UserBehaviorService userBehaviorService;
	final
	LinkService linkService;
	final
	UserMxgServlet userMxgServlet;
	
	@GetMapping("/testHasLink/{rId}")
	public ReturnResult<BuyResourcesUserDTO> testBuyStatus(@PathVariable("rId") int rId) {
		Users loginUser = getLoginUser(true);
		return GlobalLock.safeExecute(loginUser.getUserId(), () -> {
			ReturnResult<UserResourceRepository> userResource = userBehaviorService.getUserResource(loginUser.getUserId(), rId);
			if (!userResource.isOperationResult())
				return ReturnResult.isFalse(userResource.getMsg());
			BuyResourcesUserDTO buyResourcesUserDTO = new BuyResourcesUserDTO();
			UserResourceRepository r = userResource.getReturnResult();
			buyResourcesUserDTO.setResourceId(rId);
			buyResourcesUserDTO.setDownload(r.getHasDown());
			buyResourcesUserDTO.setLinks(r.getHasLink());
			return ReturnResult.isTrue("获取成功", buyResourcesUserDTO);
		});
	}
	
	@PostMapping("/buyLink")
	@Transactional
	public ReturnResult<String> buyLink(
			@RequestBody BuyLinkRequest buyLinkRequest,
			HttpServletRequest request,
			HttpServletResponse response
	) {
		boolean isLinks = buyLinkRequest.getIsLinks();
		boolean isDownload = buyLinkRequest.getIsDownload();
		Users loginUser = getLoginUser(true);
		return GlobalLock.safeExecute(loginUser.getUserId(), () -> {
			
			if (!isLinks && !isDownload)
				throw new OperationException("购买出错了,请您至少选择一种资源下载方式?");
			ReturnResult<UserResourceRepository> userResource = userBehaviorService.getUserResource(loginUser.getUserId(), buyLinkRequest.getResourceId());
			boolean hasLink = userResource.isOperationResult() && userResource.getReturnResult().getHasLink();
			boolean hasDown = userResource.isOperationResult() && userResource.getReturnResult().getHasDown();
			if (isLinks && isDownload)
				return userBehaviorService.buyAll(loginUser.getUserId(), buyLinkRequest.getResourceId());
			if (hasDown && hasLink)
				return ReturnResult.isTrue("您已经拥有了全部的下载权限,请直接获取资源,请勿重复购买", "您已经拥有了全部的下载权限,请直接获取资源,请勿重复购买");
			//在没有链接权限且需要购买时才进行
			ReturnResult<String> buyLinkReturnResult = null;
			ReturnResult<String> buyDownReturnResult = null;
			if (isLinks) {
				buyLinkReturnResult = userBehaviorService.buyOutsideDown(loginUser.getUserId(), buyLinkRequest.getResourceId());
			}
			if (isDownload) {
				buyDownReturnResult = userBehaviorService.buyDown(loginUser.getUserId(), buyLinkRequest.getResourceId());
			}
			UsersWithBLOBs u = userMxgServlet.getItselfMxg(loginUser.getUserId());
			ElseUtil.setUserMxgCookie(response, u,
			                          (int) (jwtUtil.getTokenExpireTime(ElseUtil.getToken(request)).getTime() -
			                                 System.currentTimeMillis()) / 1000);
			return buyDownReturnResult == null ? buyLinkReturnResult : buyDownReturnResult;
		});
	}
	
	@GetMapping("/getLinks")
	public ReturnResult<DataWithUserMsg<LinksWithBLOBs>> getLinks(@RequestParam("isAll") boolean isAll, @RequestParam("rId") int rId) throws SessionExceptions {
		Users loginUser = getLoginUser(true);
		return GlobalLock.safeExecute(loginUser.getUserId(), () -> {
			
			ReturnResult<UserResourceRepository> userResource = userBehaviorService.getUserResource(loginUser.getUserId(), rId);
			if (!userResource.isOperationResult())
				return ReturnResult.isFalse(userResource.getMsg());
			//检查有没有权限
			if (!userResource.getReturnResult().getHasLink())
				return ReturnResult.isFalse("您还没有获取外部资源的下载权限呢~~");
			List<LinksWithBLOBs> linkList = linkService.getLink(rId, isAll, loginUser.getUserId(), false);
			if (linkList == null || linkList.isEmpty())
				return ReturnResult.isTrue("链接获取失败,因为没找到任何相关链接", null);
			List<DataWithUserMsg<LinksWithBLOBs>> l = new ArrayList<>();
			for (LinksWithBLOBs links : linkList)
				l.add(new DataWithUserMsg<>(links, links.getLinkUpUser(), userMxgServlet));
			return ReturnResult.isTrue("获取成功!如果页面未显示获取页面请刷新", l, -1);
		});
	}
	
	@GetMapping("/getDown/{rId}")
	public ReturnResult<DataWithUserMsg<ResourcesFileMap>> getDown(@PathVariable("rId") int rId) {
		Users loginUser = getLoginUser(true);
		return GlobalLock.safeExecute(loginUser.getUserId(), () -> {
			ReturnResult<UserResourceRepository> userResource = userBehaviorService.getUserResource(loginUser.getUserId(), rId);
			if (!userResource.isOperationResult())
				return ReturnResult.isFalse(userResource.getMsg());
			//检查有没有权限
			if (!userResource.getReturnResult().getHasDown())
				return ReturnResult.isFalse("您还没有获取本地资源的下载权限呢~~");
			List<ResourcesFileMap> li = resourcesService.getResourceFileList(rId);
			List<DataWithUserMsg<ResourcesFileMap>> list = new ArrayList<>();
			for (ResourcesFileMap r : li) {
				list.add(new DataWithUserMsg<>(r, r.getUpUser(), userMxgServlet));
				r.setFileName((new File(r.getFileName())).getName());
			}
			if (!list.isEmpty())
				return ReturnResult.isTrue("获取成功!如果页面未显示获取按钮请刷新", list, -1);
			else
				return ReturnResult.isFalse("获取失败!因为当前没有任何资源,如果您发现您购买时扣除了积分,请联系管理员");
		});
	}
	
}
