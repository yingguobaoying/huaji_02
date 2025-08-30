package com.huaji.galgamebyhuaji.dao;

import com.huaji.galgamebyhuaji.model.ResourceStatics;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface ResourceStatisticsMapper {

	List<ResourceStatics> getResourceStatics();
}