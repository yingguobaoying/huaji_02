package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersExample;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UsersMapper {
	
	long countByExample (UsersExample example);
	
	int deleteByExample (UsersExample example);
	
	int deleteByPrimaryKey (Integer userId);
	
	int insert (UsersWithBLOBs row);
	
	int insertSelective (UsersWithBLOBs row);
	
	List<UsersWithBLOBs> selectByExampleWithBLOBs (UsersExample example);
	
	List<Users> selectByExample (UsersExample example);
	
	UsersWithBLOBs selectByPrimaryKey (Integer userId);
	
	int updateByExampleSelective (@Param("row") UsersWithBLOBs row, @Param("example") UsersExample example);
	
	int updateByExampleWithBLOBs (@Param("row") UsersWithBLOBs row, @Param("example") UsersExample example);
	
	int updateByExample (@Param("row") Users row, @Param("example") UsersExample example);
	
	int updateByPrimaryKeySelective (UsersWithBLOBs row);
	
	int updateByPrimaryKeyWithBLOBs (UsersWithBLOBs row);
	
	int updateByPrimaryKey (Users row);
	
	List<UsersWithBLOBs> login (UsersWithBLOBs users);
	
	int userClockIn (@Param("userId") int userId, @Param("checkInScore") int checkInScore);
	
	List<Users> testRegisterMxg (@Param("users") Users users);
	
	
	@MapKey("userId")
	Map<Integer, Users> getAllUserListMxg ();
	
	Users getUserJurisdiction (@Param("userId") int userId);
	
	UsersWithBLOBs getUserPublic (@Param("userId") Integer usersId);
	
	Users getUserLogMxg (@Param("userId") int userId);
	
	int setUserHeadPortraitUrl (@Param("userId") int userId, @Param("userHeadPortraitUrl") String userHeadPortraitUrl);
	
	int buyResources (@Param("userId") int userId, @Param("rId") int rId,@Param("isDown") boolean isDown);
}