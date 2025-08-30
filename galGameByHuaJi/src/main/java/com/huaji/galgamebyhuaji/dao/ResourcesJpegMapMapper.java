package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.ResourcesJpegMap;
import com.huaji.galgamebyhuaji.entity.ResourcesJpegMapExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResourcesJpegMapMapper {
    long countByExample(ResourcesJpegMapExample example);

    int deleteByExample(ResourcesJpegMapExample example);

    int insert(ResourcesJpegMap row);

    int insertSelective(ResourcesJpegMap row);

    List<ResourcesJpegMap> selectByExample(ResourcesJpegMapExample example);

    int updateByExampleSelective(@Param("row") ResourcesJpegMap row, @Param("example") ResourcesJpegMapExample example);

    int updateByExample(@Param("row") ResourcesJpegMap row, @Param("example") ResourcesJpegMapExample example);
}