package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.Tag;
import com.huaji.galgamebyhuaji.entity.TagExample;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper

public interface TagMapper {
	long countByExample(TagExample example);

	int deleteByExample(TagExample example);

	int deleteByPrimaryKey(Integer tagId);

	int insert(Tag row);

	int insertSelective(Tag row);

	List<Tag> selectByExample(TagExample example);

	Tag selectByPrimaryKey(Integer tagId);

	int updateByExampleSelective(@Param("row") Tag row, @Param("example") TagExample example);

	int updateByExample(@Param("row") Tag row, @Param("example") TagExample example);

	int updateByPrimaryKeySelective(Tag row);

	int updateByPrimaryKey(Tag row);

	@MapKey("tagId")
	Map<Integer, Tag> getTagMap();
	
	int addResourcesTag (@Param("TagList") List<Integer> rTags,@Param("rId")int rId);
}