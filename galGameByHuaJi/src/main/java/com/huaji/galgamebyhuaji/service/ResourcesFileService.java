package com.huaji.galgamebyhuaji.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 专门管理资源文件以及资源信文件夹息的类,由于之后预计添加的次数和规模不大,直接循环调用接口就好
 */
public interface ResourcesFileService {
	/**
	 * 上传资源图片
	 */
	String addResourceImg(List<MultipartFile> file,int rId);
	
	/**
	 * 添加资源文件夹
	 */
	String addResourceFile(List<MultipartFile> file,int rId);
	
	/**
	 * 更新资源文件夹
	 */
	String UpdateResourceFile( List<MultipartFile> file, int rId);
	
}
