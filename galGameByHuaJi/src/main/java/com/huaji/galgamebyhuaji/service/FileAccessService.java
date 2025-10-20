package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface FileAccessService {
	/**
	 * 单文件下载
	 */
	ReturnResult<ResponseEntity<InputStreamResource>> downloadFile(String fileName, FileCategory type, String downloadName, Integer users,int rId)
			throws IOException, OperationException;
	
	
	/**
	 * 删除文件
	 */
	ReturnResult<String> deleteFiles(String fileName, String fileUrl)
			throws OperationException;
	
	/**
	 * 批量删除文件
	 */
	ReturnResult<String> deleteFiles(List<String> fileNames, String fileUrl)
			throws OperationException;
}
