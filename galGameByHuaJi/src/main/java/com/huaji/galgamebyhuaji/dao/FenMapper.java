package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.Fen;
import com.huaji.galgamebyhuaji.entity.FenExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface FenMapper {
    long countByExample(FenExample example);

    int deleteByExample(FenExample example);

    int deleteByPrimaryKey(Integer rId);

    int insert(Fen row);

    int insertSelective(Fen row);

    List<Fen> selectByExample(FenExample example);

    Fen selectByPrimaryKey(Integer rId);

    int updateByExampleSelective(@Param("row") Fen row, @Param("example") FenExample example);

    int updateByExample(@Param("row") Fen row, @Param("example") FenExample example);

    int updateByPrimaryKeySelective(Fen row);

    int updateByPrimaryKey(Fen row);
}