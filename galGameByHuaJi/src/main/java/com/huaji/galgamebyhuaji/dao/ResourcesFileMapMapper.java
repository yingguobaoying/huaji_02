package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.ResourcesFileMap;
import com.huaji.galgamebyhuaji.entity.ResourcesFileMapExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ResourcesFileMapMapper {
    long countByExample(ResourcesFileMapExample example);

    int deleteByExample(ResourcesFileMapExample example);

    int insert(ResourcesFileMap row);

    int insertSelective(ResourcesFileMap row);

    List<ResourcesFileMap> selectByExampleWithBLOBs(ResourcesFileMapExample example);

    List<ResourcesFileMap> selectByExample(ResourcesFileMapExample example);

    int updateByExampleSelective(@Param("row") ResourcesFileMap row, @Param("example") ResourcesFileMapExample example);

    int updateByExampleWithBLOBs(@Param("row") ResourcesFileMap row, @Param("example") ResourcesFileMapExample example);

    int updateByExample(@Param("row") ResourcesFileMap row, @Param("example") ResourcesFileMapExample example);
	
	int insertAll(List<ResourcesFileMap> rf);
    int hasFile(Integer rId);
}
