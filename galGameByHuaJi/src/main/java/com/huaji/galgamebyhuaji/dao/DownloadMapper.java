package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.Download;
import com.huaji.galgamebyhuaji.entity.DownloadExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface DownloadMapper {
    long countByExample(DownloadExample example);

    int deleteByExample(DownloadExample example);

    int insert(Download row);

    int insertSelective(Download row);

    List<Download> selectByExample(DownloadExample example);

    int updateByExampleSelective(@Param("row") Download row, @Param("example") DownloadExample example);

    int updateByExample(@Param("row") Download row, @Param("example") DownloadExample example);
}