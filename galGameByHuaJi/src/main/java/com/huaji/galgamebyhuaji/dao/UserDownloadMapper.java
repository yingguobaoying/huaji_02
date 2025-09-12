package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.UserDownload;
import com.huaji.galgamebyhuaji.entity.UserDownloadExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface UserDownloadMapper {
    long countByExample(UserDownloadExample example);

    int deleteByExample(UserDownloadExample example);

    int insert(UserDownload row);

    int insertSelective(UserDownload row);

    List<UserDownload> selectByExample(UserDownloadExample example);

    int updateByExampleSelective(@Param("row") UserDownload row, @Param("example") UserDownloadExample example);

    int updateByExample(@Param("row") UserDownload row, @Param("example") UserDownloadExample example);
}
