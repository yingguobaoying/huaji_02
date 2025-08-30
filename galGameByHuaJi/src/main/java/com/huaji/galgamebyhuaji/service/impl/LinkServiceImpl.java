package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.dao.LinksMapper;
import com.huaji.galgamebyhuaji.entity.Links;
import com.huaji.galgamebyhuaji.entity.LinksExample;
import com.huaji.galgamebyhuaji.entity.LinksWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.LinksEnum;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LinkServiceImpl implements LinkService {
	@Autowired
	LinksMapper linksMapper;
	
	public List<LinksWithBLOBs> getLink (Integer rId, boolean isAll, int userId, boolean isAbout) {
		if ( isAbout ) isAll = false;
		if ( rId == null || rId < 0 )
			throw new OperationException("不存在的资源!");
		LinksExample linksExample = new LinksExample();
		LinksExample.Criteria criteria = linksExample.createCriteria();
		criteria.andLinkREqualTo(rId);
		if ( !isAll ) {
			criteria.andLinkStateIn(List.of(LinksEnum.ok.getName(), LinksEnum.unstable.getName()));
		}
		return linksMapper.selectByExampleWithBLOBs(linksExample);
	}
	
	public LinksWithBLOBs addLink (LinksWithBLOBs link) {
		return null;
	}
	
	@Override
	public Links dleLink (Long linkId) {
		return null;
	}
	
	@Override
	public LinksWithBLOBs getLinks (Integer userId, boolean isRoot) {
		return null;
	}
	
	@Override
	public Links upDateLink (LinksWithBLOBs link) {
		return null;
	}
	
	@Override
	public Map<Integer, Integer> getStatisticsMxg () {
		return Map.of();
	}
	
	@Override
	public String changeLinkStability (Long linkId, boolean isRoot, LinksEnum level) {
		return "";
	}
}