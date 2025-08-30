package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.ResourceExtensionInformation;
import com.huaji.galgamebyhuaji.entity.ResourceExtensionInformationExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

public interface ResourceExtensionInformationMapper {
    long countByExample(ResourceExtensionInformationExample example);

    int deleteByExample(ResourceExtensionInformationExample example);

    int deleteByPrimaryKey(Integer rId);

    int insert(ResourceExtensionInformation row);

    int insertSelective(ResourceExtensionInformation row);

    List<ResourceExtensionInformation> selectByExample(ResourceExtensionInformationExample example);

    ResourceExtensionInformation selectByPrimaryKey(Integer rId);

    int updateByExampleSelective(@Param("row") ResourceExtensionInformation row, @Param("example") ResourceExtensionInformationExample example);

    int updateByExample(@Param("row") ResourceExtensionInformation row, @Param("example") ResourceExtensionInformationExample example);

    int updateByPrimaryKeySelective(ResourceExtensionInformation row);

    int updateByPrimaryKey(ResourceExtensionInformation row);
}