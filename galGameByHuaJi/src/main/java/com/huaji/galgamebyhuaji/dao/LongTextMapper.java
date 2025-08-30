package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.LongText;
import com.huaji.galgamebyhuaji.entity.LongTextExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LongTextMapper {
    long countByExample(LongTextExample example);

    int deleteByExample(LongTextExample example);

    int insert(LongText row);

    int insertSelective(LongText row);

    List<LongText> selectByExampleWithBLOBs(LongTextExample example);

    List<LongText> selectByExample(LongTextExample example);

    int updateByExampleSelective(@Param("row") LongText row, @Param("example") LongTextExample example);

    int updateByExampleWithBLOBs(@Param("row") LongText row, @Param("example") LongTextExample example);

    int updateByExample(@Param("row") LongText row, @Param("example") LongTextExample example);
}