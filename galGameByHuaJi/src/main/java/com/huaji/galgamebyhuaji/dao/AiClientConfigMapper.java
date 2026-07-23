package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.AiClientConfig;
import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface AiClientConfigMapper {
    long countByExample(AiClientConfigExample example);
    
    int deleteByExample(AiClientConfigExample example);
    
    int deleteByPrimaryKey(Long id);
    
    int insert(AiClientConfigWithBLOBs row);
    
    int insertSelective(AiClientConfigWithBLOBs row);
    
    List<AiClientConfigWithBLOBs> selectByExampleWithBLOBs(AiClientConfigExample example);
    
    List<AiClientConfig> selectByExample(AiClientConfigExample example);
    
    AiClientConfigWithBLOBs selectByPrimaryKey(Long id);
    
    int updateByExampleSelective(@Param("row") AiClientConfigWithBLOBs row, @Param("example") AiClientConfigExample example);
    
    int updateByExampleWithBLOBs(@Param("row") AiClientConfigWithBLOBs row, @Param("example") AiClientConfigExample example);
    
    int updateByExample(@Param("row") AiClientConfig row, @Param("example") AiClientConfigExample example);
    
    int updateByPrimaryKeySelective(AiClientConfigWithBLOBs row);
    
    int updateByPrimaryKeyWithBLOBs(AiClientConfigWithBLOBs row);
    
    int updateByPrimaryKey(AiClientConfig row);
    
    List<AiClientConfigWithBLOBs> getUserUseModer();
}
