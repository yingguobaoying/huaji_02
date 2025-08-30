package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.FriendMap;
import com.huaji.galgamebyhuaji.entity.FriendMapExample;
import com.huaji.galgamebyhuaji.entity.FriendMapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface FriendMapMapper {
    long countByExample(FriendMapExample example);

    int deleteByExample(FriendMapExample example);

    int deleteByPrimaryKey(FriendMapKey key);

    int insert(FriendMap row);

    int insertSelective(FriendMap row);

    List<FriendMap> selectByExample(FriendMapExample example);

    FriendMap selectByPrimaryKey(FriendMapKey key);

    int updateByExampleSelective(@Param("row") FriendMap row, @Param("example") FriendMapExample example);

    int updateByExample(@Param("row") FriendMap row, @Param("example") FriendMapExample example);

    int updateByPrimaryKeySelective(FriendMap row);

    int updateByPrimaryKey(FriendMap row);
}