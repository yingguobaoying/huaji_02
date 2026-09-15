package com.huaji.galgamebyhuaji.service.impl;


import com.huaji.galgamebyhuaji.dao.TagMapper;
import com.huaji.galgamebyhuaji.entity.Tag;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.service.RedisMemoryService;
import com.huaji.galgamebyhuaji.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceIMPL implements TagService {
	private final TagMapper tagMapper;
	private final RedisMemoryService redisMemoryService;
	
	@Override
	public ReturnResult<Tag> addTag (Tag tag) {
		WriteError.tryWrite(tagMapper.insertSelective(tag));//更新数据库
		redisMemoryService.saveData(tag);
		return ReturnResult.isTrue("添加成功", tag);
	}
	
	
	@Override
	public ReturnResult<Tag> updateTag (Tag tag) {
		Tag tag1 = tagMapper.selectByPrimaryKey(tag.getTagId());
		WriteError.tryWrite(tagMapper.updateByPrimaryKey(tag));
		MyLogUtil.info(TagService.class, "更新前的内容:" + tag1);
		MyLogUtil.info(TagService.class, "更新后的内容:" + tag);
		redisMemoryService.saveData(tag);
		return new ReturnResult<Tag>().operationTrue("更新成功", tag);
	}
	
	@Override
	public ReturnResult<Tag> deleteTag (Integer id) {
		Tag tag = tagMapper.selectByPrimaryKey(id);
		if ( tag == null )
			return new ReturnResult<Tag>().operationFalse("错误!不存在的tag");
		int i = tagMapper.deleteByPrimaryKey(id);
		if ( i == 1 ) {
			redisMemoryService.deleteKey(tag.getTagId(), Tag.class);
			return new ReturnResult<Tag>().operationTrue("删除成功", tag);
		}
		throw new WriteError(1, i);
	}
	
	@Override
	public Map<Integer, Tag> getTagMap () {
		Map<Integer, Tag> tagMap = redisMemoryService.getTagMap();
		if ( tagMap == null || tagMap.isEmpty() ){
			tagMap = tagMapper.getTagMap();
			redisMemoryService.setKey(tagMap);
		}
		return tagMap;
	}
}
