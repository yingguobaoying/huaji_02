package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.AiClassification;
import com.huaji.galgamebyhuaji.entity.AiClassificationExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface AiClassificationMapper {
    long countByExample(AiClassificationExample example);

    int deleteByExample(AiClassificationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AiClassification row);

    int insertSelective(AiClassification row);

    List<AiClassification> selectByExample(AiClassificationExample example);

    AiClassification selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") AiClassification row, @Param("example") AiClassificationExample example);

    int updateByExample(@Param("row") AiClassification row, @Param("example") AiClassificationExample example);

    int updateByPrimaryKeySelective(AiClassification row);

    int updateByPrimaryKey(AiClassification row);
    
    List<AiClassification> getUserViewList(@Param("userId") int userId);
    
    int userCanSee(@Param("userId") int userId, @Param("configId") Long configId);
    
    List<Integer> getUserIdsByConfigId(@Param("config") long config);
}
