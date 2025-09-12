package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.ResourcesFileMap;
import com.huaji.galgamebyhuaji.entity.ResourcesFileMapExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface ResourcesFileMapMapper {
    long countByExample(ResourcesFileMapExample example);

    int deleteByExample(ResourcesFileMapExample example);

    int insert(ResourcesFileMap row);

    int insertSelective(ResourcesFileMap row);

    List<ResourcesFileMap> selectByExample(ResourcesFileMapExample example);

    int updateByExampleSelective(@Param("row") ResourcesFileMap row, @Param("example") ResourcesFileMapExample example);

    int updateByExample(@Param("row") ResourcesFileMap row, @Param("example") ResourcesFileMapExample example);
	
	int hasFile(@Param("rId") int rId);
	
}
