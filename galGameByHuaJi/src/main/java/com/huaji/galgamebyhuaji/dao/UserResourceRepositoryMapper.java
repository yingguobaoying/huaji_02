package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.entity.UserResourceRepository;
import com.huaji.galgamebyhuaji.entity.UserResourceRepositoryExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserResourceRepositoryMapper {
	long countByExample(UserResourceRepositoryExample example);
	
	int deleteByExample(UserResourceRepositoryExample example);
	
	int insert(UserResourceRepository row);
	
	int insertSelective(UserResourceRepository row);
	
	List<UserResourceRepository> selectByExample(UserResourceRepositoryExample example);
	
	int updateByExampleSelective(@Param("row") UserResourceRepository row, @Param("example") UserResourceRepositoryExample example);
	
	int updateByExample(@Param("row") UserResourceRepository row, @Param("example") UserResourceRepositoryExample example);
	
	List<UserResourceRepository> getUserResource(@Param("userId") int userId);
	
	int userHasDownResource(@Param("rId") int rId, @Param("userId") int userId);
	
	int userHasLinkResource(@Param("rId") int rId, @Param("userId") int userId);
}
