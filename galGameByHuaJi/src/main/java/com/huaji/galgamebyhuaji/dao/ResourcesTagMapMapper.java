package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.ResourcesTagMapExample;
import com.huaji.galgamebyhuaji.entity.ResourcesTagMapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ResourcesTagMapMapper {
    long countByExample(ResourcesTagMapExample example);

    int deleteByExample(ResourcesTagMapExample example);

    int deleteByPrimaryKey(ResourcesTagMapKey key);

    int insert(ResourcesTagMapKey row);

    int insertSelective(ResourcesTagMapKey row);

    List<ResourcesTagMapKey> selectByExample(ResourcesTagMapExample example);

    int updateByExampleSelective(@Param("row") ResourcesTagMapKey row, @Param("example") ResourcesTagMapExample example);

    int updateByExample(@Param("row") ResourcesTagMapKey row, @Param("example") ResourcesTagMapExample example);

    int addResourcesTag(@Param("TagList") List<ResourcesTagMapKey> resourcesTagMapKeys);

}