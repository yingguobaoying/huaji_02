package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 专门管理资源文件以及资源信文件夹息的类,由于之后预计添加的次数和规模不大,直接循环调用接口就好
 */
public interface ResourcesFileService {
	/**
	 * 上传资源图片
	 */
	String addResourceImg(List<MultipartFile> file, int rId, boolean hasFirst) throws IOException;
	
	String updateResourceImg(List<MultipartFile> file, int rId, boolean hasFirst) throws IOException;
	
	//删除文件
	String delResourceFile(List<String> fileName, int rId, FileCategory fileType);
	
	/**
	 * 添加资源文件夹
	 *
	 * @param addFile 是否以资源名称新建一个文件夹,命名格式:yyyy_MM_dd_hh_mm_rName
	 */
	String addResourceFile(List<MultipartFile> file, int rId, boolean addFile);
	
}
