package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.dao.LinksMapper;
import com.huaji.galgamebyhuaji.dao.ResourcesFileMapMapper;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.Comment;
import com.huaji.galgamebyhuaji.entity.Links;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.model.jwtToken.BuyResourcesUser;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserBehaviorService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 请注意此服务类不会检查用户身份信息,请在调用前检查
 *
 * @author 滑稽/因果报应
 */
@Service
@Transactional
public class UserBehaviorServiceImpl implements UserBehaviorService {
	final
	UsersMapper usersMapper;
	final
	UserMxgServlet userMxgServlet;
	final
	TokenService tokenService;
	final
	LinksMapper linksMapper;
	final
	ResourcesFileMapMapper resourcesFileMap;
	
	public UserBehaviorServiceImpl(UsersMapper usersMapper, UserMxgServlet userMxgServlet, TokenService tokenService, LinksMapper linksMapper, ResourcesFileMapMapper resourcesFileMap) {
		this.usersMapper = usersMapper;
		this.userMxgServlet = userMxgServlet;
		this.tokenService = tokenService;
		this.linksMapper = linksMapper;
		this.resourcesFileMap = resourcesFileMap;
	}
	
	@Override
	public ReturnResult<String> buyOutsideDown(Integer userId, Integer rId, String ip) throws SessionExceptions {
		//检查有没有外部链接
		if (!(linksMapper.testLink(rId) > 0)) {
			throw new OperationException("购买未能成功，因为该资源暂未录入外部下载链接。非常抱歉给您带来不便。");
		}
		
		//进行购买
		int actual = usersMapper.buyResources(userId, rId, false);
		if (actual == 0) throw new OperationException("购买失败，您的积分余额不足。");
		
		if (actual == 1) {
			//购买成功,生成令牌
			BuyResourcesUser user = new BuyResourcesUser();
			user.setUserId(userId);
			user.setResourceId(rId);
			user.setIp(ip);
			user.setLinks(true);
			user.setDownload(false);
			UserToken userToken = tokenService.insertToken(
					user,
					TokenType.GET_DOWNLOAD,
					Constant.RESOURCE_EXPIRATION_TIME * 60 * 60 * 1000L);
			
			//返回令牌
			return ReturnResult.isTrue(
					"您成功购买了该资源的外部下载权限。",
					userToken.getToken()
			);
		}
		throw new WriteError(1, 1);
	}
	
	
	@Override
	public ReturnResult<String> buyDown(Integer userId, Integer rId, String ip) throws SessionExceptions {
		//检查有没有本地文件
		if (!(resourcesFileMap.hasFile(rId) > 0)) {
			throw new OperationException("购买未能成功，因为该资源暂未录入本地下载文件。");
		}
		
		//进行购买
		int actual = usersMapper.buyResources(userId, rId, true);
		if (actual == 0) throw new OperationException("购买失败，您的积分余额不足。");
		
		if (actual == 1) {
			//购买成功,生成令牌
			BuyResourcesUser user = new BuyResourcesUser();
			user.setUserId(userId);
			user.setResourceId(rId);
			user.setIp(ip);
			user.setLinks(false);
			user.setDownload(true);
			UserToken userToken = tokenService.insertToken(
					user,
					TokenType.GET_DOWNLOAD,
					Constant.RESOURCE_EXPIRATION_TIME * 60 * 60 * 1000L
			);
			
			//返回令牌
			return ReturnResult.isTrue(
					"您购买了该资源的本地下载权限。",
					userToken.getToken()
			);
		}
		throw new WriteError(1, 1);
	}
	
	
	@Override
	public ReturnResult<String> buyAll(Integer userId, Integer rId, String ip) throws SessionExceptions {
		boolean hasFile = resourcesFileMap.hasFile(rId) > 0;
		boolean hasLink = linksMapper.testLink(rId) > 0;
		
		if (!hasFile && !hasLink) {
			throw new OperationException("购买未能成功，因为该资源暂未录入任何下载方式。");
		}
		
		// 尝试购买所需的全部资源
		int resultDownload = hasFile ? usersMapper.buyResources(userId, rId, true) : 1;
		int resultLink = hasLink ? usersMapper.buyResources(userId, rId, false) : 1;
		
		if (resultDownload == 0 || resultLink == 0) {
			throw new OperationException("购买失败，您的积分余额不足，暂时无法获取所需的全部下载权限。");
		}
		
		if (resultDownload == 1 && resultLink == 1) {
			BuyResourcesUser user = new BuyResourcesUser();
			user.setUserId(userId);
			user.setResourceId(rId);
			user.setIp(ip);
			user.setLinks(hasLink);
			user.setDownload(hasFile);
			
			UserToken userToken = tokenService.insertToken(
					user,
					TokenType.GET_DOWNLOAD,
					Constant.RESOURCE_EXPIRATION_TIME * 60 * 60 * 1000L
			);
			
			String msg;
			if (hasFile && hasLink) {
				msg = "您成功购买了该资源的【本地下载】和【外部链接】权限。";
			}
			else if (hasFile) {
				msg = "您已成功购买【本地下载】权限！由于该资源暂未录入外部链接，小站未收取您额外的积分。";
			}
			else {
				msg = "您已成功购买【外部链接】权限！由于该资源暂未录入本地文件，小站未收取您额外的积分。";
			}
			return ReturnResult.isTrue(msg, userToken.getToken());
		}
		
		throw new WriteError(1, resultDownload + resultLink);
	}
	
	
	@Override
	public List<Links> getUserUpLink(Users users) {
		return List.of();
	}
	
	@Override
	public List<Comment> getUserComment(Users users) {
		return List.of();
	}
}
