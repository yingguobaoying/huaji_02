package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.entity.LinksWithBLOBs;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.model.jwtToken.BuyResourcesUser;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.model.jwtToken.TokenMsg;
import com.huaji.galgamebyhuaji.myUtil.*;
import com.huaji.galgamebyhuaji.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@ResponseBody
@RequestMapping("/api/user")
public class BuyResourcesController extends BaseController {
	@Autowired
	ResourcesService resourcesService;
	@Autowired
	TokenService tokenService;
	@Autowired
	JWTUtil jwtUtil;
	@Autowired
	UserBehaviorService userBehaviorService;
	@Autowired
	LinkService linkService;
	@Autowired
	UserMxgServlet userMxgServlet;
	
	@GetMapping("/testHasLink")
	public ReturnResult<BuyResourcesUser> testBuyStatus (HttpServletRequest request) throws SessionExceptions {
		Users loginUser = getLoginUser(true);
		String token = ElseUtil.getToken(request, SystemConstant.USER_BUY_TOKEN);
		if ( MyStringUtil.isNull(token) ) return ReturnResult.isFalse("未发现令牌");
		OnlineUser onlineUser = tokenService.VerifyAndParse(token, loginUser.getUserId(), TokenType.GET_DOWNLOAD, ElseUtil.getClientIp(request));
		if ( onlineUser == null ) return ReturnResult.isFalse("无效的令牌");
		if ( onlineUser instanceof BuyResourcesUser user ) {
			return ReturnResult.isTrue("令牌有效", user);
		} else {
			return ReturnResult.isFalse("无效的令牌");
		}
	}
	
	@GetMapping("/buyLink")
	@Transactional
	public ReturnResult<TokenMsg> buyLink (HttpServletRequest request,
	                                       HttpServletResponse response,
	                                       @RequestParam("isLinks") boolean isLinks,
	                                       @RequestParam("isDownload") boolean isDownload,
	                                       @RequestParam("resourceId") int resourceId) throws SessionExceptions {
		Users loginUser = getLoginUser(true);
		if ( !isLinks && !isDownload )
			throw new OperationException("购买出错了,请您至少选择一种资源下载方式?");
		if ( resourceId < 0 ) return ReturnResult.isFalse("无效的资源id");
		//检查有没有令牌
		String token = ElseUtil.getToken(request, SystemConstant.USER_BUY_TOKEN);
		boolean hasToken = false;
		String ip = ElseUtil.getClientIp(request);
		String message = "";
		Date expireDate;
		if ( !MyStringUtil.isNull(token) ) {
			try {
				//验证和解析
				OnlineUser cache = tokenService.VerifyAndParse(token, loginUser.getUserId(), TokenType.GET_DOWNLOAD, ip);
				if ( cache instanceof BuyResourcesUser user ) {
					hasToken = true;
				}
			} catch ( OperationException e ) {
				MyLogUtil.error(BuyResourcesController.class, "发现用户用户{%d}:{%s}进行拥有购买资源{%d}后发生的令牌,但是令牌解析时出错:%s".formatted(
						loginUser.getUserId(), loginUser.getUserName(), resourceId, e.getMsg()));
			} catch ( SessionExceptions e ) {
				MyLogUtil.error(BuyResourcesController.class, "发现用户用户{%d}:{%s}进行拥有购买资源{%d}后发生的令牌,但是令牌解析时出错:%s".formatted(
						loginUser.getUserId(), loginUser.getUserName(), resourceId, e.getMessage()));
			}
		}
		if ( !hasToken ) {
			//跳过购买流程
			ReturnResult<String> stringReturnResult;
			if ( isLinks && isDownload )
				stringReturnResult = userBehaviorService.buyAll(loginUser.getUserId(), resourceId, ip);
			else if ( isDownload )
				stringReturnResult = userBehaviorService.buyDown(loginUser.getUserId(), resourceId, ip);
			else stringReturnResult = userBehaviorService.buyOutsideDown(loginUser.getUserId(), resourceId, ip);
			message = stringReturnResult.getMxg();
			token = stringReturnResult.getReturnResult();
			expireDate = TimeUtil.getFutureTimeByHour(Constant.RESOURCE_EXPIRATION_TIME);
		} else {
			String s1 = "【本地下载】";
			String s2 = "【外链下载】";
			String sb;
			if ( isLinks && isDownload )
				sb = s1 + "和" + s2;
			else if ( isLinks )
				sb = s2;
			else
				sb = s1;
			message = "您已拥有%s的下载权限,请直接使用即可,无需再次购买".formatted(sb);
		}
		if ( MyStringUtil.isNull(token) ) {
			MyLogUtil.error(BuyResourcesController.class, "用户{%d}:{%s}进行购买资源{%d}失败,可能扣除了积分".formatted(
					loginUser.getUserId(), loginUser.getUserName(), resourceId));
			return ReturnResult.isFalse("购买过程出错,令牌生成失败了,请联系及时联系管理员进行处理!请检查您的积分是否发生了错误!");
		}//双重保证:请求头添加新令牌,以及返回新令牌
		expireDate = jwtUtil.getTokenExpireTime(token);
		ElseUtil.setUserMxgCookie(response, userMxgServlet.getItselfMxg(loginUser.getUserId()), (int) (expireDate.getTime() - System.currentTimeMillis()) / 1000);
		request.setAttribute(SystemConstant.USER_BUY_TOKEN, token);
		request.getSession().setAttribute(SystemConstant.USER_BUY_TOKEN, token);
		return ReturnResult.isTrue(message, new TokenMsg(expireDate, token));
	}
	
	@GetMapping("/getLinks/{rId}")
	public ReturnResult<LinksWithBLOBs> getLinks (HttpServletRequest request, @PathVariable("rId") int rId, @RequestParam("isAll") boolean isAll) throws SessionExceptions {
		Users loginUser = getLoginUser(true);
		String token = ElseUtil.getToken(request, SystemConstant.USER_BUY_TOKEN);
		OnlineUser onlineUser = tokenService.VerifyAndParse(token, loginUser.getUserId(), TokenType.GET_DOWNLOAD, ElseUtil.getClientIp(request));
		if ( onlineUser instanceof BuyResourcesUser user ) {
			if ( user.isLinks() ) {
				return ReturnResult.isTrue("获取成功", linkService.getLink(rId, isAll, loginUser.getUserId(), false), -1);
			} else {
				return ReturnResult.isFalse("您还没有购买外部链接下载权限");
			}
		} else return ReturnResult.isFalse("出错了,请检查您提供的令牌,令牌类型不符");
	}
}