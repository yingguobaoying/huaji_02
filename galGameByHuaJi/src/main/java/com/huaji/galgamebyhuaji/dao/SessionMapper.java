package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.Session;
import com.huaji.galgamebyhuaji.entity.SessionExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface SessionMapper {
	long countByExample(SessionExample example);
	
	int deleteByExample(SessionExample example);
	
	int deleteByPrimaryKey(Long sessionId);
	
	int insert(Session row);
	
	int insertSelective(Session row);
	
	List<Session> selectByExample(SessionExample example);
	
	Session selectByPrimaryKey(Long sessionId);
	
	int updateByExampleSelective(@Param("row") Session row, @Param("example") SessionExample example);
	
	int updateByExample(@Param("row") Session row, @Param("example") SessionExample example);
	
	int updateByPrimaryKeySelective(Session row);
	
	int updateByPrimaryKey(Session row);
	
	int helicopterAirCrash();
	
	List<Session> getSessionByUser(@Param("userId") int userId, @Param("ip") String loginIP);
	
	int exitLogin(@Param("usersId") Integer usersId);
}
