package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.ResourcesExample;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourcesMapper {
	long countByExample(ResourcesExample example);

	int deleteByExample(ResourcesExample example);

	int deleteByPrimaryKey(Integer rId);

	int insert(Resources row);

	int insertSelective(Resources row);

	List<Resources> selectByExampleWithBLOBs(ResourcesExample example);

	List<Resources> selectByExample(ResourcesExample example);

	Resources selectByPrimaryKey(Integer rId);

	int updateByExampleSelective(@Param("row") Resources row, @Param("example") ResourcesExample example);

	int updateByExampleWithBLOBs(@Param("row") Resources row, @Param("example") ResourcesExample example);

	int updateByExample(@Param("row") Resources row, @Param("example") ResourcesExample example);

	int updateByPrimaryKeySelective(Resources row);

	int updateByPrimaryKeyWithBLOBs(Resources row);

	int updateByPrimaryKey(Resources row);

	@MapKey("rId")
	Map<Integer, Resources> gatResourcesMap();

	List<Resources> selectPage(@Param("start") int start, @Param("count") int count);

	Integer getResourceListSize();
}