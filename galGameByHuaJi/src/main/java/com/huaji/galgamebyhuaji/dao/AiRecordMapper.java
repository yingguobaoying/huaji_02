package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.AiRecord;
import com.huaji.galgamebyhuaji.entity.AiRecordExample;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface AiRecordMapper {
    long countByExample(AiRecordExample example);

    int deleteByExample(AiRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AiRecordWithBLOBs row);

    int insertSelective(AiRecordWithBLOBs row);

    List<AiRecordWithBLOBs> selectByExampleWithBLOBs(AiRecordExample example);

    List<AiRecord> selectByExample(AiRecordExample example);

    AiRecordWithBLOBs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") AiRecordWithBLOBs row, @Param("example") AiRecordExample example);

    int updateByExampleWithBLOBs(@Param("row") AiRecordWithBLOBs row, @Param("example") AiRecordExample example);

    int updateByExample(@Param("row") AiRecord row, @Param("example") AiRecordExample example);

    int updateByPrimaryKeySelective(AiRecordWithBLOBs row);

    int updateByPrimaryKeyWithBLOBs(AiRecordWithBLOBs row);

    int updateByPrimaryKey(AiRecord row);
    
    List<AiRecordWithBLOBs> getLatestBySize(@Param("size") int size, @Param("userId") int userId, @Param("sessionId") String sessionId);
    
    List<AiRecordWithBLOBs> getFirstRecord(@Param("userId") int userId);
    
    List<AiRecordWithBLOBs> getRecord(@Param("userId") int userId, @Param("sessionId") String sessionId);
    
    int selectSession (@Param("sessionId") String sessionId);
}