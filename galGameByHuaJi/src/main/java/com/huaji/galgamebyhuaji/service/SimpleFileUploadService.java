package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.model.ReturnResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 由于之前的文件服务类过于复杂,这里新开一个上传类
 */
public interface SimpleFileUploadService {
	ReturnResult<String> uploadFiles(List<MultipartFile> files, String fileType, List<String> names)
			throws IOException;
}
