package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.FileServlet;
import com.huaji.galgamebyhuaji.service.SimpleFileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.huaji.galgamebyhuaji.constant.Constant.*;

@Service
public class SimpleFileUploadServiceImpl extends FileServletImpl implements SimpleFileUploadService {
	
	
	/**
	 * 批量上传文件
	 *
	 * @param files    上传的文件列表
	 * @param fileType 文件类型（jpeg/zip）
	 * @param names    自定义文件名列表（可选）
	 */
	public ReturnResult<String> uploadFiles(List<MultipartFile> files, String fileType, List<String> names) {
		
		if (files == null || files.isEmpty()) {
			throw new OperationException("文件列表不能为空");
		}
		
		List<String> savedFiles = new ArrayList<>();
		try {
			for (int i = 0; i < files.size(); i++) {
				MultipartFile file = files.get(i);
				String customName = (names != null && i < names.size() && !MyStringUtil.isNull(names.get(i)))
						? names.get(i)
						: null;
				String finalName = uploadSingleFile(file, fileType, customName);
				savedFiles.add(finalName);
			}
		} catch (Exception e) {
			MyLogUtil.error(FileServlet.class, e);
			for (String fileName : savedFiles) {
				try {
					deleteFile(fileName, basePath);
				} catch (OperationException ex) {
					MyLogUtil.error(FileServletImpl.class, ex);
				}
			}
			throw new OperationException("上传失败，已回滚已上传文件");
		}
		
		return ReturnResult.isTrue("上传成功", savedFiles, -1);
	}
	
	/**
	 * 处理单个文件上传
	 */
	private String uploadSingleFile(MultipartFile file, String fileType, String customName)
			throws IOException {
		
		if (file.isEmpty()) {
			throw new OperationException("文件为空");
		}
		
		// 生成安全文件名
		String fileName = generateFileName(customName);
		
		return switch (fileType.toLowerCase()) {
			case "jpeg" -> {
				validateFileSize(file, JPEG_MIN, JPEG_MAX);
				fileName += ".jpeg";
				saveImage(file, fileName);
				yield fileName;
			}
			case "zip" -> {
				validateFileSize(file, RAT_MIN, RAR_MAX);
				String archiveType = detectArchiveType(file);
				if (archiveType == null) {
					throw new OperationException("不支持的压缩文件类型");
				}
				String finalName = fileName + "." + archiveType;
				saveArchive(file, finalName);
				yield finalName;
			}
			default -> throw new OperationException("不支持的文件类型：" + fileType);
		};
	}
	
	/**
	 * 生成安全的文件名
	 */
	private String generateFileName(String name) {
		if (MyStringUtil.isNull(name)) {
			return "upload_" + TimeUtil.getNowTime();
		}
		return name.replaceAll("[\\\\/:*?\"<>|]", "_");
	}
	
}
