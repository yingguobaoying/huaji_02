package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.UserTokenExample;
import com.huaji.galgamebyhuaji.entity.UserTokenKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserTokenMapper {
	long countByExample(UserTokenExample example);

	int deleteByExample(UserTokenExample example);

	int deleteByPrimaryKey(UserTokenKey key);

	int insert(UserToken row);

	int insertSelective(UserToken row);

	List<UserToken> selectByExampleWithBLOBs(UserTokenExample example);

	List<UserToken> selectByExample(UserTokenExample example);

	UserToken selectByPrimaryKey(UserTokenKey key);

	int updateByExampleSelective(@Param("row") UserToken row, @Param("example") UserTokenExample example);

	int updateByExampleWithBLOBs(@Param("row") UserToken row, @Param("example") UserTokenExample example);

	int updateByExample(@Param("row") UserToken row, @Param("example") UserTokenExample example);

	int updateByPrimaryKeySelective(UserToken row);

	int updateByPrimaryKeyWithBLOBs(UserToken row);

	int updateByPrimaryKey(UserToken row);

	List<UserToken> verifyToken(@Param("token") String token, @Param("userId") int userId, @Param("type") int type);

	int invalidateToken(@Param("token") String token, @Param("userId") int userId, @Param("type") int type, @Param("time") Date time);

	List<UserToken> getTokens(@Param("userId") int userId, @Param("type") int type);
}