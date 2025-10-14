package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {
	/**
	 * 单文件上传
	 */
	ReturnResult<String> uploadFile(MultipartFile file, FileCategory fileType, String fileName, String sumPath)
			throws IOException, OperationException;
	
	/**
	 * 批量上传
	 */
	ReturnResult<String> uploadFiles(List<MultipartFile> files, FileCategory fileType, List<String> fileNames, String sumPath)
			throws IOException, OperationException;
}
