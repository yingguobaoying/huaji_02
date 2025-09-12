package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.dao.ResourcesMapper;
import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.PageUtil;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import com.huaji.galgamebyhuaji.service.SelectServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectServletImpl implements SelectServlet {
	@Autowired
	ResourcesMapper resourcesMapper;
	@Autowired
	ResourcesService resourcesService;
	
	
	@Override
	public int getSearchResourceSize(Resources rMsg, List<Integer> tag, int tagSize) {
		tagSize = Math.min(tag.size(), tagSize);
		return resourcesMapper.getSelectResourcesSize(rMsg, tag, tagSize);
	}
	
	@Override
	public List<Resources> searchResource(Resources rMsg, List<Integer> tag, int tagSize, PageUtil pageMsg) {
		tagSize = Math.min(tag.size(), tagSize);
		rMsg = MyStringUtil.setNull(rMsg);
		return resourcesMapper.selectResources(rMsg, tag, tagSize, pageMsg);
	}
	
	@Override
	public List<Users> searchUser(Integer uId, String uName, PageUtil pageMsg) {
		return List.of();
	}
}
