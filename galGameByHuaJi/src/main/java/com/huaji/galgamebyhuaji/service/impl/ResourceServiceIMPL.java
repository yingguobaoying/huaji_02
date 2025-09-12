package com.huaji.galgamebyhuaji.service.impl;


import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.dao.*;
import com.huaji.galgamebyhuaji.entity.*;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ResourceStatics;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.RedisMemoryService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import com.huaji.galgamebyhuaji.service.TagService;
import com.huaji.galgamebyhuaji.vo.SelectViewMag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceServiceIMPL implements ResourcesService {
	@Autowired
	ResourcesMapper resourcesMapper;
	@Autowired
	ResourcesTagMapMapper resourcesTagMapMapper;
	@Autowired
	ResourcesJpegMapMapper resourcesJpegMapMapper;
	@Autowired
	ResourceExtensionInformationMapper resourceExtensionInformationMapper;
	@Autowired
	RedisMemoryService redisMemoryService;
	@Autowired
	TagService tagService;
	@Autowired
	ResourceStatisticsMapper resourceStatisticsMapper;
	@Autowired
	TagMapper tagMapper;
	@Autowired
	ResourcesFileMapMapper resourcesFileMap;
	
	@Override
	public ReturnResult<Resources> addResources(Resources resources) {
		testNewResourcesMxg(resources);
		//主键由数据库管理,这里保证只要不重复就可以进行添加,防止错误传递
		resources.setrId(null);
		WriteError.tryWrite(resourcesMapper.insertSelective(resources));
		if (resources.getrId() == null) throw new WriteError(1, 0);
		ResourceExtensionInformation resourceExtensionInformation = resources.getResourceExtensionInformation();
		//无价格信息
		if (resourceExtensionInformation == null) {
			resourceExtensionInformation = new ResourceExtensionInformation();
			resourceExtensionInformation.setrId(resources.getrId());
			resourceExtensionInformation.setDownloadLocallyPrice(Constant.DOWNLOAD_LOCALLY);
			resourceExtensionInformation.setLinkPrice(Constant.EXTERNAL_CLOUD_DISK);
			resourceExtensionInformation.setHasDownloadLocally("no");
		} else {
			resourceExtensionInformation.setrId(resources.getrId());
			if (resourceExtensionInformation.getDownloadLocallyPrice() == null)
				resourceExtensionInformation.setDownloadLocallyPrice(Constant.DOWNLOAD_LOCALLY);
			if (resourceExtensionInformation.getLinkPrice() == null)
				resourceExtensionInformation.setLinkPrice(Constant.EXTERNAL_CLOUD_DISK);
			//均不匹配或者为空时使用默认值
			if (
					!(
							"yes".equals(resourceExtensionInformation.getHasDownloadLocally())
									|| "no".equals(resourceExtensionInformation.getHasDownloadLocally())
					) || MyStringUtil.isNull(resourceExtensionInformation.getHasDownloadLocally())
			)
				resourceExtensionInformation.setHasDownloadLocally("no");
			
		}
		//添加tag
		if (resources.getTags() != null && !resources.getTags().isEmpty()) {
			List<Integer> rTags = new ArrayList<>();
			for (Tag tag : resources.getTags()) {
				rTags.add(tag.getTagId());
			}
			tagMapper.addResourcesTag(rTags, resources.getrId());
		}
		
		WriteError.tryWrite(resourceExtensionInformationMapper.insert(resourceExtensionInformation));
		redisMemoryService.saveData(resources);
		return ReturnResult.isTrue("资源信息插入成功", resources);
	}
	
	private ReturnResult<Resources> testNewResourcesMxg(Resources resources) {
		ResourcesExample resourcesExample = new ResourcesExample();
		resourcesExample.createCriteria()
				.andRManufacturerEqualTo(resources.getrManufacturer())
				.andRNameEqualTo(resources.getrName());
		List<Resources> resources1 = resourcesMapper.selectByExample(resourcesExample);
		if (resources1.isEmpty()) return new ReturnResult<Resources>().operationTrue("资源信息可用", null);
		if (resources1.size() == 1 && resources1.getFirst().getrId().equals(resources.getrId()))
			return new ReturnResult<Resources>().operationTrue("资源信息可用", null);
		throw new OperationException("资源的资源名称和资源厂家/发行商重复");
	}
	
	@Override
	public ReturnResult<Resources> updateResources(Resources resources) {
		ReturnResult<Resources> resourcesReturnResult = testNewResourcesMxg(resources);
		if (!resourcesReturnResult.isOperationResult()) return resourcesReturnResult;
		WriteError.tryWrite(resourcesMapper.updateByPrimaryKeyWithBLOBs(resources));
		//更新tag信息
		ResourcesTagMapExample resourcesTagMapExample = new ResourcesTagMapExample();
		resourcesTagMapExample.createCriteria().andRIdEqualTo(resources.getrId());
		resourcesTagMapMapper.deleteByExample(resourcesTagMapExample);
		if (resources.getTags() != null && !resources.getTags().isEmpty()) {//更新数据库中的映射关系
			List<Integer> rTags = new ArrayList<>();
			for (Tag tag : resources.getTags()) rTags.add(tag.getTagId());
			WriteError.tryWrite(tagMapper.addResourcesTag(rTags, resources.getrId()), resources.getTags().size());
		}
		redisMemoryService.saveData(resources);
		return new ReturnResult<Resources>().operationTrue("资源更新成功", resources);
	}
	
	@Override
	public ReturnResult<Resources> deleteResources(Integer rId) {
		//更新数据库
		Resources resources = resourcesMapper.selectByPrimaryKey(rId);
		ResourcesTagMapExample example = new ResourcesTagMapExample();
		example.createCriteria().andRIdEqualTo(rId);
		WriteError.tryWrite(resourcesTagMapMapper.deleteByExample(example), resources.getTags().size());
		WriteError.tryWrite(resourcesMapper.deleteByPrimaryKey(rId));
		redisMemoryService.deleteKey(rId, Resources.class);
		return new ReturnResult<Resources>().operationTrue("资源成功删除", resources);
	}
	
	@Override
	public List<Resources> getAllResources() {
		//  从数据库获取所有资源及相关信息
		Map<Integer, Resources> resourcesMap = resourcesMapper.gatResourcesMap();
		
		List<ResourcesTagMapKey> resourcesTagMapKeys = resourcesTagMapMapper.selectByExample(null);
		List<ResourcesJpegMap> resourcesJpegMaps = resourcesJpegMapMapper.selectByExample(null);
		List<ResourceExtensionInformation> resourceExtensionInformations = resourceExtensionInformationMapper.selectByExample(null);
		
		Map<Integer, Tag> tagMap = tagService.getTagMap();
		
		// 预分组
		Map<Integer, List<ResourcesTagMapKey>> tagGroup = resourcesTagMapKeys.stream()
				.collect(Collectors.groupingBy(ResourcesTagMapKey::getrId));
		
		Map<Integer, List<ResourcesJpegMap>> jpegGroup = resourcesJpegMaps.stream()
				.collect(Collectors.groupingBy(ResourcesJpegMap::getResourcesId));
		
		Map<Integer, ResourceExtensionInformation> extensionMap = resourceExtensionInformations.stream()
				.collect(Collectors.toMap(ResourceExtensionInformation::getrId, e -> e));
		
		//  并行处理资源对象
		resourcesMap.values().parallelStream().forEach(res -> {
			
			//局部构建 Tag 列表
			List<Tag> tagList = Optional.ofNullable(tagGroup.get(res.getrId()))
					.orElse(Collections.emptyList())
					.stream()
					.map(t -> tagMap.get(t.getTagId()))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			res.setTags(tagList); // 一次性设置，避免并发问题
			
			//局部构建 JPEG 列表
			List<String> jpegList = Optional.ofNullable(jpegGroup.get(res.getrId()))
					.orElse(Collections.emptyList())
					.stream()
					.map(ResourcesJpegMap::getJpegName)
					.collect(Collectors.toList());
			res.setrPicture(jpegList); // 一次性设置
			
			//设置拓展信息
			ResourceExtensionInformation ext = extensionMap.get(res.getrId());
			if (ext != null) {
				res.setResourceExtensionInformation(ext);
			}
		});
		
		//  转换为 ArrayList 并写入 Redis,虽然不转化也行,但是方法返回时仍然需要转换,这里就直接转化了避免重复
		List<Resources> resources = new ArrayList<>(resourcesMap.values());
		redisMemoryService.setKey(resources);
		
		return resources;
	}
	
	@Override
	public ReturnResult<SelectViewMag> getResourceStatics() {
		List<ResourceStatics> resourceStatics = resourceStatisticsMapper.getResourceStatics();
		Map<String, Long> result = new HashMap<>();
		for (ResourceStatics r : resourceStatics) {
			result.put(r.getrType(), r.getSize());
		}
		SelectViewMag selectViewMag = new SelectViewMag();
		if (resourceStatics.isEmpty()) return ReturnResult.isFalse("全局统计信息获取失败!");
		selectViewMag.setResourceStatistics(result);
		return ReturnResult.isTrue("全局统计信息获取成功!", selectViewMag);
	}
	
	@Override
	public Resources getResource(int rId) {
		Resources resources = redisMemoryService.getData(rId, Resources.class);
		if (resources == null || resources.getrId() == null) {
			resources = resourcesMapper.selectByPrimaryKey(rId);
			redisMemoryService.saveData(resources);
		}
		if (resources == null || resources.getrId() == null) throw new OperationException("您请求的资源不存在!");
		return resources;
	}
	
	@Override
	public List<Resources> getResourceList(int start, int end) {
		List<Resources> resources = redisMemoryService.getPagedResources(start, end);
		if (resources == null || resources.isEmpty())
			resources = resourcesMapper.selectPage(start, end - start + 1);
		if (resources == null || resources.isEmpty()) throw new OperationException("您请求的资源不存在!");
		return resources;
	}
	
	@Override
	public Integer getResourceListSize() {
		int size = redisMemoryService.getResourceListSize();
		if (size == 0) size = resourcesMapper.getResourceListSize();
		return size;
	}
	
	@Override
	public List<ResourcesFileMap> getResourceFileList(int rId) {
		if (rId < 0) throw new OperationException("错误!不存在的资源信息");
		ResourcesFileMapExample example = new ResourcesFileMapExample();
		example.createCriteria()
				.andRIdEqualTo(rId);
		List<ResourcesFileMap> list = resourcesFileMap.selectByExample(example);
		if (list == null || list.isEmpty())
			return List.of();
		else
			return list;
	}
}
