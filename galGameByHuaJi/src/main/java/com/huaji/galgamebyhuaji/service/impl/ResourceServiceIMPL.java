package com.huaji.galgamebyhuaji.service.impl;


import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.dao.*;
import com.huaji.galgamebyhuaji.entity.*;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ResourceStatics;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.FileUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.FileAccessService;
import com.huaji.galgamebyhuaji.service.RedisMemoryService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import com.huaji.galgamebyhuaji.service.TagService;
import com.huaji.galgamebyhuaji.vo.SelectViewMag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ResourceServiceIMPL implements ResourcesService {
	final
	ResourcesMapper resourcesMapper;
	final
	ResourcesTagMapMapper resourcesTagMapMapper;
	final
	ResourcesJpegMapMapper resourcesJpegMapMapper;
	final
	ResourceExtensionInformationMapper resourceExtensionInformationMapper;
	final
	RedisMemoryService redisMemoryService;
	final
	TagService tagService;
	final
	ResourceStatisticsMapper resourceStatisticsMapper;
	final
	TagMapper tagMapper;
	final
	ResourcesFileMapMapper resourcesFileMap;
	final
	FileAccessService fileAccessService;
	
	@Override
	@Transactional
	public ReturnResult<Resources> addResources(Resources resources, List<Integer> tags) {
		testNewResourcesMxg(resources);
		resources.setrId(null);
		WriteError.tryWrite(resourcesMapper.insertSelective(resources));
		if (resources.getrId() == null) throw new WriteError(1, 0);
		ResourceExtensionInformation resourceExtensionInformation = getResourceExtensionInformation(resources, true);
		//添加tag
		if (tags != null && !tags.isEmpty()) {
			Map<Integer, Tag> tagMap = tagService.getTagMap();
			tagMapper.addResourcesTag(tags, resources.getrId());
			for (Integer tagId : tags) {
				Tag tag = tagMap.get(tagId);
				resources.addTag(tag);
			}
		}
		resources.setResourceExtensionInformation(resourceExtensionInformation);
		WriteError.tryWrite(resourceExtensionInformationMapper.insertSelective(resourceExtensionInformation));
		redisMemoryService.saveData(resources);
		return ReturnResult.isTrue("资源信息插入成功", resources);
	}
	
	private ResourceExtensionInformation getResourceExtensionInformation(Resources resources, boolean isAdd) {
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
			//添加时使用默认值
			if (isAdd)
				resourceExtensionInformation.setHasDownloadLocally("no");
			
		}
		return resourceExtensionInformation;
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
	@Transactional
	public ReturnResult<Resources> updateResources(Resources resources, List<Integer> tags) {
		ReturnResult<Resources> resourcesReturnResult = testNewResourcesMxg(resources);
		Resources oldMxg = resourcesMapper.selectByPrimaryKey(resources.getrId());
		MyLogUtil.info(ResourcesService.class, "资源信息发生变动,原先信息如下:" + oldMxg);
		if (!resourcesReturnResult.isOperationResult()) return resourcesReturnResult;
		WriteError.tryWrite(resourcesMapper.updateByPrimaryKeyWithBLOBs(resources));
		//更新tag信息
		ResourcesTagMapExample resourcesTagMapExample = new ResourcesTagMapExample();
		resourcesTagMapExample.createCriteria().andRIdEqualTo(resources.getrId());
		resourcesTagMapMapper.deleteByExample(resourcesTagMapExample);
		//更新资源拓展信息
		ResourceExtensionInformation resourceExtensionInformation = getResourceExtensionInformation(resources, false);
		resources.setResourceExtensionInformation(resourceExtensionInformation);
		WriteError.tryWrite(resourceExtensionInformationMapper.updateByPrimaryKey(resourceExtensionInformation));
		if (tags != null && !tags.isEmpty()) {//更新数据库中的映射关系
			WriteError.tryWrite(tagMapper.addResourcesTag(tags, resources.getrId()), tags.size());
			TagExample tagExample = new TagExample();
			tagExample.createCriteria().andTagIdIn(tags);
			List<Tag> tags1 = tagMapper.selectByExample(tagExample);
			resources.setTags(tags1);
		}
		redisMemoryService.saveData(resources);
		return new ReturnResult<Resources>().operationTrue("资源更新成功", resources);
	}
	
	@Override
	@Transactional
	public ReturnResult<Resources> deleteResources(Integer rId) {
		//更新数据库
		Resources resources = resourcesMapper.selectByPrimaryKey(rId);
		ResourcesTagMapExample example = new ResourcesTagMapExample();
		example.createCriteria().andRIdEqualTo(rId);
		WriteError.tryWrite(resourcesTagMapMapper.deleteByExample(example), resources.getTags().size());
		WriteError.tryWrite(resourcesMapper.deleteByPrimaryKey(rId));
		//删除图片,这里不删除上传的文件信息,而是保留,因为文件可能被多个资源使用(比如某些多合一版本)
		fileAccessService.deleteFiles(resources.getrPicture(), (new File(Constant.getRESOURCE_SAVE_PATH(), FileCategory.IMG.getFILE_SAVE_URL())).getPath());
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
					.map(ResourcesJpegMap::getJpegName
					)
					.collect(Collectors.toList());
			res.setrPicture(jpegList); // 一次性设置
			
			//设置拓展信息
			ResourceExtensionInformation ext = extensionMap.get(res.getrId());
			if (ext != null) {
				res.setResourceExtensionInformation(ext);
			}
			res.setrJpeg(FileUtil.toRelativeUrl(res.getrJpeg(), FileCategory.IMG));
		});
		
		//  转换为 ArrayList 并写入 Redis
		List<Resources> resources = new ArrayList<>(resourcesMap.values());
		redisMemoryService.setKey(resources);
		return resources;
	}
	
	@Override
	public ReturnResult<SelectViewMag> getResourceStatics() {
		List<ResourceStatics> resourceStatics = resourceStatisticsMapper.getResourceStatics();
		Map<String, Long> result = new HashMap<>();
		for (ResourceStatics r : resourceStatics) {
			result.put(r.getRType(), r.getSize());
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
			//获取图片
			ResourcesJpegMapExample resourcesJpegMapExample = new ResourcesJpegMapExample();
			resourcesJpegMapExample.createCriteria()
					.andResourcesIdEqualTo(rId);
			List<ResourcesJpegMap> resourcesJpegMaps = resourcesJpegMapMapper.selectByExample(resourcesJpegMapExample);
			List<String> rImg = new ArrayList<>();
			resourcesJpegMaps.forEach(rj -> rImg.add(FileUtil.toRelativeUrl(rj.getJpegName(), FileCategory.IMG)));
			resources.setrPicture((rImg));
			resources.setResourceExtensionInformation(resourceExtensionInformationMapper.selectByPrimaryKey(rId));
			resources.setrJpeg(FileUtil.toRelativeUrl(resources.getrJpeg(), FileCategory.IMG));
			//获取tag
			ResourcesTagMapExample resourcesTagMapExample = new ResourcesTagMapExample();
			resourcesTagMapExample.createCriteria().andRIdEqualTo(rId);
			List<ResourcesTagMapKey> resourcesTagIdList = resourcesTagMapMapper.selectByExample(resourcesTagMapExample);
			Map<Integer, Tag> tagMap = tagService.getTagMap();
			resources.setTags(
					resourcesTagIdList.stream().map(tagId -> tagMap.get(tagId.getTagId())).toList()
			);
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
	
	@Override
	public List<String> getRType() {
		String typeString = resourcesMapper.getrType();
		if (MyStringUtil.isNull(typeString))
			return List.of();
		Pattern pattern = Pattern.compile("'([^']*)'");
		Matcher matcher = pattern.matcher(typeString);
		List<String> values = new ArrayList<>();
		while (matcher.find())
			values.add(matcher.group(1));
		return values;
	}
	
	private void updateResourceImgUrl(Resources r) {
		r.setrJpeg(FileUtil.toRelativeUrl(r.getrJpeg(), FileCategory.IMG));
		List<String> urls = r.getrPicture();
		for (String url : urls) {
			url = FileUtil.toRelativeUrl(url, FileCategory.IMG);
		}
		r.setrPicture(urls);
	}
}
