package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.CommentMapper;
import com.huaji.galgamebyhuaji.dao.LinksMapper;
import com.huaji.galgamebyhuaji.dao.ResourcesFileMapMapper;
import com.huaji.galgamebyhuaji.dao.UserResourceRepositoryMapper;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.*;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.UserResourceUtil;
import com.huaji.galgamebyhuaji.service.UserBehaviorService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import com.huaji.galgamebyhuaji.vo.CommentWithUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 请注意此服务类不会检查用户身份信息,请在调用前检查
 *
 * @author 滑稽/因果报应
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserBehaviorServiceImpl implements UserBehaviorService {
	private final UserResourceRepositoryMapper mapper;
	private final ResourcesFileMapMapper resourcesFileMap;
	private final UsersMapper usersMapper;
	private final UserResourceUtil util;
	private final LinksMapper linksMapper;
	private final CommentMapper commentMapper;
	private final UserMxgServlet mxgServlet;
	
	public ReturnResult<UserResourceRepository> getUserResource(Integer userId, Integer rId) {
		if (userId == null || userId < 0)
			return ReturnResult.isFalse("用户不存在!");
		if (rId == null || rId < 0)
			return ReturnResult.isFalse("资源不存在!");
		UserResourceRepositoryExample e = new UserResourceRepositoryExample();
		e.createCriteria().andUserIdEqualTo(userId).andRIdEqualTo(rId).andExpirationTimeGreaterThanOrEqualTo(new Date());
		List<UserResourceRepository> userResourceList = mapper.selectByExample(e);
		if (userResourceList == null || userResourceList.isEmpty())
			return ReturnResult.isFalse("该用户没有此资源的权限");
		//到这里应该是有权限的
		UserResourceRepository r = trimUserResourceList(userId, rId, userResourceList);
		if (!(r.getHasLink() || r.getHasDown()))
			return ReturnResult.isFalse("该用户没有此资源的权限");
		return ReturnResult.isTrue("获取成功!", r);
	}
	
	private static UserResourceRepository trimUserResourceList(Integer userId, Integer rId, List<UserResourceRepository> userResourceList) {
		UserResourceRepository r = new UserResourceRepository();
		r.setGetTime(new Date());
		r.setrId(rId);
		r.setUserId(userId);
		boolean hasDown = false;
		boolean hasLink = false;
		Date max = null;
		for (UserResourceRepository ur : userResourceList) {
			if (ur.getHasDown())
				hasDown = true;
			if (ur.getHasLink())
				hasLink = true;
			if (max == null || max.before(ur.getExpirationTime()))
				max = ur.getExpirationTime();
		}
		r.setHasDown(hasDown);
		r.setHasLink(hasLink);
		r.setExpirationTime(max);
		return r;
	}
	
	@Override
	public ReturnResult<String> buyDown(Integer userId, Integer rId) {
		return GlobalLock.safeExecute(userId, () -> {
			//检查有没有本地文件
			if (!(resourcesFileMap.hasFile(rId) > 0)) {
				return ReturnResult.isFalse("购买未能成功，因为该资源暂未录入任何文件信息。非常抱歉给您带来不便。");
			}
			util.hasUserAndResource(userId, rId);
			int i = mapper.userHasDownResource(rId, userId);
			if (i != 0)
				return ReturnResult.isTrue("您已经购买了此资源,请勿重复购买,如果您没有在页面上看到购买选项请手动刷新一下页面", "您已经购买了此资源,请勿重复购买,如果您没有在页面上看到购买选项请手动刷新一下页面");
			//进行购买
			int actual = usersMapper.buyResources(userId, rId, true);
			if (actual == 0) throw new OperationException("购买失败，您的积分余额不足。");
			if (actual != 1)
				throw new WriteError(1, actual);
			UserResourceRepository userResourceRepository = newObject(userId, rId);
			userResourceRepository.setHasDown(true);
			userResourceRepository.setHasLink(false);
			WriteError.tryWrite(mapper.insert(userResourceRepository));
			//购买成功
			MyLogUtil.info(UserBehaviorServiceImpl.class, "id为{%d}的用户购买了id为{%d}的资源本地资源下载权限".formatted(userId, rId));
			return ReturnResult.isTrue("购买成功", "购买成功");
		});
	}
	
	private UserResourceRepository newObject(Integer userId, Integer rId) {
		UserResourceRepository userResourceRepository = new UserResourceRepository();
		userResourceRepository.setExpirationTime(new Date(
				System.currentTimeMillis() + Constant.RESOURCE_EXPIRATION_TIME));
		userResourceRepository.setUserId(userId);
		userResourceRepository.setrId(rId);
		userResourceRepository.setGetTime(new Date());
		return userResourceRepository;
	}
	
	@Override
	public ReturnResult<String> buyOutsideDown(Integer userId, Integer rId) {
		return GlobalLock.safeExecute(userId, () -> {
			//检查有没有外部链接
			if (!(linksMapper.testLink(rId) > 0)) {
				throw new OperationException("购买未能成功，因为该资源暂未录入外部下载链接。非常抱歉给您带来不便。");
			}
			util.hasUserAndResource(userId, rId);
			int i = mapper.userHasDownResource(rId, userId);
			if (i != 0)
				return ReturnResult.isFalse("您已经购买了此资源,请勿重复购买,如果您没有在页面上看到购买选项请手动刷新一下页面");
			//进行购买
			int actual = usersMapper.buyResources(userId, rId, false);
			if (actual == 0) throw new OperationException("购买失败，您的积分余额不足。");
			if (actual != 1)
				throw new WriteError(1, actual);
			UserResourceRepository userResourceRepository = newObject(userId, rId);
			userResourceRepository.setHasDown(false);
			userResourceRepository.setHasLink(true);
			WriteError.tryWrite(mapper.insert(userResourceRepository));
			//购买成功
			MyLogUtil.info(UserBehaviorServiceImpl.class, "id为{%d}的用户购买了id为{%d}的资源外部资源下载权限".formatted(userId, rId));
			return ReturnResult.isTrue("购买成功", "购买成功");
		});
	}
	
	@Override
	public ReturnResult<String> buyAll(Integer userId, Integer rId) {
		return GlobalLock.safeExecute(userId, () -> {
			
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
			String msg;
			if (hasFile && hasLink) {
				msg = "您成功购买了该资源的【本地下载】和【外部链接】权限。";
			} else if (hasFile) {
				msg = "您已成功购买【本地下载】权限！由于该资源暂未录入外部链接，小站未收取您额外的积分。";
			} else {
				msg = "您已成功购买【外部链接】权限！由于该资源暂未录入本地文件，小站未收取您额外的积分。";
			}
			//插入数据库
			UserResourceRepository u = newObject(userId, rId);
			u.setHasLink(hasLink);
			u.setHasDown(hasFile);
			WriteError.tryWrite(mapper.insert(u));
			MyLogUtil.info(UserBehaviorServiceImpl.class, "id为{%d}的用户购买了id为{%d}的资源的全部下载权限".formatted(userId, rId));
			return ReturnResult.isTrue(msg, msg);
		});
	}
	
	@Override
	public List<Links> getUserUpLink(Users users) {
		LinksExample example = new LinksExample();
		example.createCriteria().andLinkUpUserEqualTo(users.getUserId());
		return linksMapper.selectByExample(example);
	}
	
	@Override
	public List<CommentWithUser> getUserComment(Users users) {
		CommentExample e = new CommentExample();
		e.createCriteria().andCommentUserEqualTo(users.getUserId());
		List<Comment> comments = commentMapper.selectByExampleWithBLOBs(e);
		if (comments == null || comments.isEmpty())
			return List.of();
		Users userListMsg = mxgServlet.getUserListMsg(users.getUserId());
		return comments.stream().map(r ->
				                             new CommentWithUser(r, userListMsg)
		
		).toList();
	}
}
