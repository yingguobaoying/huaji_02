package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.Feedback;
import com.huaji.galgamebyhuaji.entity.FeedbackExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface FeedbackMapper {
    long countByExample(FeedbackExample example);

    int deleteByExample(FeedbackExample example);

    int insert(Feedback row);

    int insertSelective(Feedback row);

    List<Feedback> selectByExampleWithBLOBs(FeedbackExample example);

    List<Feedback> selectByExample(FeedbackExample example);

    int updateByExampleSelective(@Param("row") Feedback row, @Param("example") FeedbackExample example);

    int updateByExampleWithBLOBs(@Param("row") Feedback row, @Param("example") FeedbackExample example);

    int updateByExample(@Param("row") Feedback row, @Param("example") FeedbackExample example);
}
