package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.dao.ResourcesFileMapMapper;
import com.huaji.galgamebyhuaji.dao.ResourcesJpegMapMapper;
import com.huaji.galgamebyhuaji.dao.ResourcesMapper;
import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.ResourcesJpegMap;
import com.huaji.galgamebyhuaji.entity.ResourcesJpegMapExample;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.FileUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.FileAccessService;
import com.huaji.galgamebyhuaji.service.FileUploadService;
import com.huaji.galgamebyhuaji.service.RedisMemoryService;
import com.huaji.galgamebyhuaji.service.ResourcesFileService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ResourcesFileServiceImpl implements ResourcesFileService {
	final FileUploadService fileUploadService;
	final FileAccessService fileAccessService;
	final ResourcesService resourcesService;
	final ResourcesMapper resourcesMapper;
	final ResourcesFileMapMapper resourcesFileMapMapper;
	final ResourcesJpegMapMapper resourcesJpegMapMapper;
	final RedisMemoryService redisMemoryService;
	
	@Override
	public String addResourceImg(List<MultipartFile> file, int rId, boolean hasFirst) throws IOException {
		if (file == null || file.isEmpty())
			throw new OperationException("上传文件为空!");
		//清除redis缓存
		redisMemoryService.deleteKey(rId, Resources.class);
		Resources resource = getResources(rId);
		String folderName =
				rId + new SimpleDateFormat("-yyyy-MM-dd").format(new Date()) + "-" + resource.getrName();
		String upAtPath = getBuildResourcePath(rId, folderName);
		//带首页图时特殊处理
		if (hasFirst) {
			ArrayList<MultipartFile> file1 = new ArrayList<>();
			file1.add(file.getLast());
			updateResourceImg(file1, rId, true);
			file.removeLast();
		}
		if (file.isEmpty())
			return "文件上传完成";
		List<String> fileName = new ArrayList<>(file.size());
		//自动生成文件名
		for (int i = 0; i < file.size(); i++) fileName.add(rId + TimeUtil.getNowTime() + "_" + i);
		ReturnResult<String> url = fileUploadService.uploadFiles(file, FileCategory.IMG, fileName, upAtPath);
		List<ResourcesJpegMap> list = new ArrayList<>(file.size());
		for (int i = 0; i < file.size(); i++) {
			ResourcesJpegMap resourcesFileMap = new ResourcesJpegMap();
			resourcesFileMap.setResourcesId(rId);
			resourcesFileMap.setJpegName(url.getResultList().get(i));
			list.add(resourcesFileMap);
		}
		try {//写入
			WriteError.tryWrite(resourcesJpegMapMapper.insertAll(list), list.size());
		} catch (Exception e) {
			fileAccessService.deleteFiles(url.getResultList(), FileUtil.formatUrl(Constant.getRESOURCE_SAVE_PATH(), FileCategory.IMG.getFILE_SAVE_URL(), upAtPath));
			throw e;
		}
		return url.getMsg();
	}
	
	private Resources getResources(int rId) {
		Resources resource = resourcesService.getResource(rId);
		if (resource == null || resource.getrId() == null)
			throw new OperationException("资源不存在!");
		return resource;
	}
	
	private String getBuildResourcePath(int rId, String resourceName) {
		//除去首页图,路径举例
		// static/img/resources/0-10000/0-100/rId-time-rName
		// static/img/resources/0-10000/101-200/rId-time-rName
		//获取基本存储路径
		StringBuffer path = new StringBuffer(
				FileUtil.formatUrl("resources")
		);
		// 万级分组 (0-10000, 10001-20000, ...)
		int tenThousandGroup = (rId - 1) / 10_000;  // 从0开始计数
		int tenThousandStart = tenThousandGroup * 10_000;
		int tenThousandEnd = tenThousandStart + 10_000;
		path.append("/").append(tenThousandStart).append("-").append(tenThousandEnd);
		// 百级分组 (0-100, 101-200, ...)
		int hundredGroup = (rId - 1) / 100;  // 从0开始计数
		int hundredStart = hundredGroup * 100;
		int hundredEnd = hundredStart + 100;
		path.append("/").append(hundredStart).append("-").append(hundredEnd);
		path.append("/")
				.append(resourceName);
		return path.toString();
	}
	
	@Override
	public String updateResourceImg(List<MultipartFile> file, int rId, boolean hasFirst) throws IOException {
		if (file == null || file.isEmpty())
			throw new OperationException("上传文件为空!");
		Resources resource = getResources(rId);
		//清除redis缓存
		redisMemoryService.deleteKey(rId, Resources.class);
		String folderName =
				rId + new SimpleDateFormat("-yyyy-MM-dd-").format(new Date()) + resource.getrName();
		String upAtPath = getBuildResourcePath(rId, folderName);
		if (hasFirst) {
			//非空时删除
			try {
				if (!MyStringUtil.isNull(resource.getrJpeg()))
					fileAccessService.deleteFiles(resource.getrJpeg(), null);
			} catch (Exception e) {
				MyLogUtil.info(ResourcesService.class, "尝试删除文件失败,原因是" + e.getMessage());
			}
			//上传文件
			ReturnResult<String> url = fileUploadService.uploadFile(
					file.getLast(),
					FileCategory.IMG,
					rId + "-first-" +
					new SimpleDateFormat("-yyyy-MM-dd-").format(new Date()) +
					resource.getrName(), upAtPath);
			Resources r = new Resources();
			r.setrId(rId);
			r.setrJpeg(url.getReturnResult());
			//最小化修改,由于资源服务类过大并且以查询为主这里就直接用map了避免过度拓展
			try {
				WriteError.tryWrite(resourcesMapper.updateByPrimaryKeySelective(r));
			} catch (Exception e) {
				delResourceFile(Collections.singletonList(url.getReturnResult()), rId, FileCategory.IMG);
				throw e;
			}
			//删除首页图
			file.removeLast();
		}
		if (file.isEmpty())
			return "更新完成";
		//删除原有的所有内容
		ResourcesJpegMapExample example = new ResourcesJpegMapExample();
		example.createCriteria().andResourcesIdEqualTo(rId);
		List<ResourcesJpegMap> resourcesJpegMaps = resourcesJpegMapMapper.selectByExample(example);
		if (!resourcesJpegMaps.isEmpty()) {
			ArrayList<String> oldJpeg = new ArrayList<>(resourcesJpegMaps.size());
			for (ResourcesJpegMap resourcesJpegMap : resourcesJpegMaps) oldJpeg.add(resourcesJpegMap.getJpegName());
			//删除原文件和数据库记录
			ReturnResult<String> stringReturnResult = fileAccessService.deleteFiles(oldJpeg, null);
			MyLogUtil.info(ResourcesService.class, "更新时旧文件处理情况:" + stringReturnResult.getMsg());
			WriteError.tryWrite(resourcesJpegMapMapper.deleteByExample(example), oldJpeg.size());
		}
		//添加记录
		return addResourceImg(file, rId, false);
	}
	
	//禁止通过系统删除资源,仅允许手动管理
	@Override
	public String delResourceFile(List<String> fileName, int rId, FileCategory fileType) {
		Resources resource = getResources(rId);
		String folderName =
				rId + "-" + new SimpleDateFormat("-yyyy-MM-dd").format(new Date()) + "-" + resource.getrName();
		String upAtPath = getBuildResourcePath(rId, folderName);
		return fileAccessService.deleteFiles(fileName, FileUtil.formatUrl(Constant.getRESOURCE_SAVE_PATH(), fileType.getFILE_SAVE_URL(), upAtPath)).getMsg();
		
	}
	
	@Override
	public String addResourceFile(List<MultipartFile> file, int rId, boolean addFile) {
		return "";
	}
	
}
