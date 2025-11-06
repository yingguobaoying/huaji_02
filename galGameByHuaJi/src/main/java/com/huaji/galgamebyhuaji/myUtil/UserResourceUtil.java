package com.huaji.galgamebyhuaji.myUtil;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResourceUtil {
	final UserMxgServlet userMxgServlet;
	final ResourcesService resourcesService;
	
	public void hasUser(Integer uId) {
		if (uId == null || uId < 0)
			throw new OperationException("不存在的用户!");
		userMxgServlet.getUserListMsg(uId);
	}
	
	public void hasResource(Integer rId) {
		if (rId == null || rId < 0)
			throw new OperationException("不存在的资源信息!");
		resourcesService.getResource(rId);
	}
	
	public void hasUserAndResource(Integer userId, Integer rId) {
		hasUser(userId);
		hasResource(rId);
	}
}
