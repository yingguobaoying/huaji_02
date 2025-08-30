package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.Links;
import com.huaji.galgamebyhuaji.entity.LinksExample;
import com.huaji.galgamebyhuaji.entity.LinksWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LinksMapper {
    long countByExample(LinksExample example);

    int deleteByExample(LinksExample example);

    int deleteByPrimaryKey(Long linkId);

    int insert(LinksWithBLOBs row);

    int insertSelective(LinksWithBLOBs row);

    List<LinksWithBLOBs> selectByExampleWithBLOBs(LinksExample example);

    List<Links> selectByExample(LinksExample example);

    LinksWithBLOBs selectByPrimaryKey(Long linkId);

    int updateByExampleSelective(@Param("row") LinksWithBLOBs row, @Param("example") LinksExample example);

    int updateByExampleWithBLOBs(@Param("row") LinksWithBLOBs row, @Param("example") LinksExample example);

    int updateByExample(@Param("row") Links row, @Param("example") LinksExample example);

    int updateByPrimaryKeySelective(LinksWithBLOBs row);

    int updateByPrimaryKeyWithBLOBs(LinksWithBLOBs row);

    int updateByPrimaryKey(Links row);
    
    int testLink(@Param("rId")int rId);
}