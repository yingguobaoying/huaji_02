package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.FileUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

/**
 * 文件上传服务实现类，处理文件上传 支持图片压缩和压缩包文件处理
 *
 * @author 滑稽/因果报应
 */
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
	/**
	 * 这里保存的均为文件类型头,仅可用于初步文件类型识别,无法识别是否存在分包
	 */
	protected static final Map<byte[], String> SUPPORTED_ARCHIVES = Map.ofEntries(
			Map.entry(new byte[]{0x50, 0x4B, 0x03, 0x04}, "zip"),
			Map.entry(new byte[]{0x52, 0x61, 0x72, 0x21}, "rar"),
			Map.entry(new byte[]{0x37, 0x7A, (byte) 0xBC}, "7z"),
			Map.entry(new byte[]{0x04, 0x22, 0x4D, 0x18}, "lz4"),
			Map.entry(new byte[]{0x1F, (byte) 0x8B}, "gz")
	);
	
	protected static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
			"image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp");
	
	/**
	 * 规范化子路径，移除不安全的字符
	 */
	private String normalizeSubPath(String subPath) {
		if (MyStringUtil.isNull(subPath)) {
			return "";
		}
		// 替换不安全的路径字符
		String safePath = subPath.replaceAll("[\\\\:*?\"<>|]", "_");
		// 移除开头的 ./ 或 .\
		safePath = safePath.replaceAll("^[./\\\\]+", "");
		// 移除结尾的 / 或 \
		safePath = safePath.replaceAll("[/\\\\]+$", "");
		return FileUtil.formatUrl(safePath);
	}
	
	/**
	 * 获取或创建目录 如果目录不存在则创建
	 *
	 * @param fileType 文件类型
	 * @param subPath  子路径
	 *
	 * @return 目录文件对象
	 *
	 * @throws IOException 目录创建失败时抛出
	 */
	protected File getOrCreateDirectory(FileCategory fileType, String subPath) throws IOException {
		File dir;
		// 确定基础目录
		if (fileType != null) {
			dir = new File(Constant.getRESOURCE_SAVE_PATH(), fileType.getFILE_SAVE_URL());
		} else {
			dir = new File(Constant.getRESOURCE_SAVE_PATH());
		}
		// 如果没有子路径，直接返回基础目录
		if (MyStringUtil.isNull(subPath)) {
			if (!dir.exists() && !dir.mkdirs()) {
				throw new IOException("无法创建目录: " + dir.getAbsolutePath());
			}
			return dir;
		}
		// 规范化子路径，防止路径遍历攻击
		subPath = normalizeSubPath(subPath);
		// 处理子路径
		String[] pathSegments = subPath.split("/");
		File currentDir = dir;
		
		// 逐级创建目录
		for (String segment : pathSegments) {
			if (!MyStringUtil.isNull(segment)) {
				currentDir = new File(currentDir, segment);
			}
		}
		validatePathSafety(currentDir);
		// 创建最终目录
		if (!currentDir.exists() && !currentDir.mkdirs()) {
			throw new IOException("无法创建目录: " + currentDir.getAbsolutePath());
		}
		return currentDir;
	}
	
	/**
	 * 验证路径安全性，防止目录遍历
	 */
	private void validatePathSafety(File targetDir) throws IOException {
		String canonicalTargetPath = targetDir.getCanonicalPath();
		String canonicalBasePath = new File(Constant.getRESOURCE_SAVE_PATH()).getCanonicalPath();
		if (!canonicalTargetPath.startsWith(canonicalBasePath)) {
			throw new IOException("非法路径: 尝试访问基础目录之外的位置");
		}
	}
	
	@Override
	public ReturnResult<String> uploadFile(MultipartFile file, FileCategory fileType, String fileName, String sumPath)
			throws IOException, OperationException {
		if (file == null || file.isEmpty())
			throw new OperationException("上传文件为空");
		// 自动生成文件名
		if (MyStringUtil.isNull(fileName)) {
			fileName = generateFileName(file, fileType);
		} else {
			fileName = sanitizeFileName(fileName, fileType);
		}
		// 根据类型保存
		switch (fileType != null ? fileType : FileCategory.OTHER) {
			case IMG:
				if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
					throw new OperationException("不支持的图片格式，仅支持: " + String.join(", ", ALLOWED_IMAGE_TYPES));
				}
				if (!fileName.endsWith(".jpeg"))
					fileName += ".jpeg";
				return saveImage(file, fileName, sumPath);
			case ARCHIVE:
				return saveArchive(file, fileName, sumPath);
			case SCRIPT, OTHER:
			default:
				// 直接保存到目录
				File targetDir = getOrCreateDirectory(fileType, sumPath);
				Path targetFile = targetDir.toPath().resolve(fileName).normalize();
				if (!targetFile.startsWith(Constant.getRESOURCE_SAVE_PATH())) {
					throw new OperationException("非法路径");
				}
				try {
					file.transferTo(targetFile);
				} catch (Exception e) {
					throw new OperationException("文件保存失败: " + e.getMessage());
				}
				MyLogUtil.info(FileUploadService.class,
				               "文件保存成功: " + targetFile.getFileName() + ", 大小=" +
				               FileUtil.formatFileSize(file.getSize()));
				return new ReturnResult<String>().operationTrue("文件保存成功", FileUtil.formatUrl(targetFile.toFile().toString()));
		}
	}
	
	@Override
	public ReturnResult<String> uploadFiles(List<MultipartFile> files, FileCategory fileType, List<String> fileNames, String sumPath)
			throws OperationException {
		if (files == null || files.isEmpty()) {
			return ReturnResult.isFalse("未提供要上传的文件列表");
		}
		//确保 fileNames 列表大小与 files 一致
		if (fileNames == null) {
			fileNames = new ArrayList<>(Collections.nCopies(files.size(), null));
		} else if (fileNames.size() < files.size()) {
			// 提供的文件名数量不足时,用null填充
			List<String> l = fileNames;
			//防止数组扩容
			fileNames = new ArrayList<>(files.size());
			fileNames.addAll(l);
			for (int i = l.size(); i < files.size(); i++) {
				fileNames.add(null);
			}
		}
		List<String> successList = new ArrayList<>();
		List<String> failList = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String fileName = i < fileNames.size() ? fileNames.get(i) : null;
			try {
				ReturnResult<String> result = uploadFile(file, fileType, fileName, sumPath);
				if (result.isOperationResult()) {
					successList.add(result.getReturnResult());
					fileNames.set(i, result.getReturnResult());
				} else {
					failList.add(fileName + "（原因：" + result.getMsg() + "）");
				}
			} catch (Exception e) {
				failList.add((fileName != null ? fileName : "未知文件") + "（原因：" + e.getMessage() + "）");
			}
		}
		if (failList.isEmpty()) {
			return ReturnResult.isTrue("全部文件上传成功", successList, null);
		} else if (successList.isEmpty()) {
			return ReturnResult.isFalse("所有文件上传失败：" + String.join(",", failList));
		} else {
			return ReturnResult.isTrue(
					"部分文件上传成功,以下为成功上传的名单,预期上传{%d}个,实际上传{%d}个".formatted(fileNames.size(), successList.size()), successList, null
			);
		}
	}
	
	
	/**
	 * 保存压缩包文件 1. 创建目标目录 2. 保存文件到指定位置 3. 错误处理：删除不完整的文件
	 *
	 * @param file     要保存的文件
	 * @param fileName 目标文件名
	 *
	 * @return 保存结果
	 *
	 * @throws IOException 文件IO异常
	 */
	protected ReturnResult<String> saveArchive(MultipartFile file, String fileName, String sumPath) throws IOException {
		Path targetDir = getOrCreateDirectory(FileCategory.ARCHIVE, sumPath).toPath();
		if (!targetDir.startsWith(Constant.getRESOURCE_SAVE_PATH())) {
			throw new IOException("非法路径");
		}
		Files.createDirectories(targetDir);
		
		Path targetFile = targetDir.resolve(fileName);
		try {
			file.transferTo(targetFile); // 直接保存
		} catch (Exception e) {
			try {// 删除可能写了一半的文件
				Files.deleteIfExists(targetFile);
			} catch (IOException ex) {
				MyLogUtil.error(FileUploadService.class, ex);
				MyLogUtil.error(FileUploadService.class, "清理传输失败残留文件失败,目标文件: " + targetFile);
			}
			MyLogUtil.error(FileUploadService.class, "存储文件时发生错误:" + e.getMessage());
			MyLogUtil.error(FileUploadService.class, e);
			throw new OperationException("文件上传失败!原因是: " + e.getMessage());
		}
		
		MyLogUtil.info(FileUploadService.class, String.format(
				"压缩文件保存成功：文件名：%s，大小：%s bytes 位置 %s",
				targetFile.getFileName(), FileUtil.formatFileSize(Files.size(targetFile)), targetFile.toFile()
		));
		return new ReturnResult<String>().operationTrue("文件保存成功", FileUtil.formatUrl(targetFile.toFile().toString()));
	}
	
	/**
	 * 检测压缩包类型 通过读取文件头部的魔数来判断文件类型
	 *
	 * @param file 要检测的文件
	 *
	 * @return 文件类型字符串，如"zip"、"rar"等，如果不支持则返回null
	 *
	 * @throws IOException 文件读取异常
	 */
	protected String detectArchiveType(MultipartFile file) throws IOException {
		String originalName = file.getOriginalFilename();
		boolean isRar = !MyStringUtil.isNull(originalName) && isRarMultiPartFile(originalName);
		
		try (InputStream is = file.getInputStream()) {
			byte[] header = new byte[10];
			int bytesRead = is.readNBytes(header, 0, header.length); // 使用 readNBytes 确保读取完整
			if (bytesRead < 4) { // 至少需要4个字节进行基本识别
				return null;
			}
			if (!isRar) {
				for (Map.Entry<byte[], String> entry : SUPPORTED_ARCHIVES.entrySet()) {
					if (startsWith(header, entry.getKey(), bytesRead)) {
						return entry.getValue();
					}
				}
			} else {
				return startsWith(header, new byte[]{0x52, 0x61, 0x72, 0x21}) ? "rar" : null;
			}
		}
		return null;
	}
	
	/**
	 * 检查字节数组是否以指定的魔数开头 用于文件类型识别
	 *
	 * @param fileHeader 文件头字节数组
	 * @param magicBytes 要匹配的魔数字节数组
	 *
	 * @return 如果匹配返回true，否则返回false
	 */
	protected boolean startsWith(byte[] fileHeader, byte[] magicBytes, int actualLength) {
		if (actualLength < magicBytes.length) {
			return false;
		}
		for (int i = 0; i < magicBytes.length; i++) {
			if (fileHeader[i] != magicBytes[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 检查字节数组是否以指定的魔数开头（使用完整数组长度）
	 */
	protected boolean startsWith(byte[] fileHeader, byte[] magicBytes) {
		return startsWith(fileHeader, magicBytes, fileHeader.length);
	}
	
	/**
	 * 将图片转换为JPEG格式 创建一个新的RGB图片，并将原图绘制到新图片上
	 *
	 * @param source 源图片
	 *
	 * @return 转换后的JPEG图片
	 */
	protected BufferedImage convertToJpeg(BufferedImage source) {
		BufferedImage jpegImage = new BufferedImage(
				source.getWidth(),
				source.getHeight(),
				BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g = jpegImage.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, source.getWidth(), source.getHeight());
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return jpegImage;
	}
	
	protected ReturnResult<String> saveImage(MultipartFile image, String fileName, String sumPath) throws IOException {
		File targetDir = getOrCreateDirectory(FileCategory.IMG, sumPath);
		
		File targetFile = new File(targetDir, fileName);
		try {
			BufferedImage sourceImage = ImageIO.read(image.getInputStream());
			if (sourceImage == null) {
				throw new OperationException("无法解析图片文件");
			}
			
			BufferedImage finalImage = convertToJpeg(sourceImage);
			
			saveAsJpeg(finalImage, targetFile);
			
			long originalSize = image.getSize();
			long compressedSize = targetFile.length();
			double compressionRatio = (double) (originalSize - compressedSize) / originalSize * 100;
			
			MyLogUtil.info(FileUploadService.class, String.format(
					"图片压缩成功：原始大小：%d bytes，压缩后大小：%s bytes，压缩率：%.2f%%，位置：%s",
					originalSize, FileUtil.formatFileSize(compressedSize), compressionRatio, targetFile
			));
			
			return ReturnResult.isTrue("图片保存成功", FileUtil.formatUrl(targetFile.toString()));
			
		} catch (Exception e) {
			if (targetFile.exists() && !targetFile.delete()) {
				MyLogUtil.error(FileUploadService.class, "无法删除临时文件: " + targetFile);
			}
			MyLogUtil.error(FileUploadService.class, e);
			throw new OperationException("图片处理失败: " + e.getMessage());
		}
	}
	
	/**
	 * 判断是否为RAR分卷文件
	 */
	protected boolean isRarMultiPartFile(String fileName) {
		if (MyStringUtil.isNull(fileName)) return false;
		return fileName.toLowerCase().matches(".*\\.part\\d+\\.rar$");
	}
	
	/**
	 * 将图片保存为JPEG格式 1. 获取JPEG图片写入器 2. 设置压缩参数 3. 写入图片数据
	 *
	 * @param image  要保存的图片
	 * @param output 输出文件
	 *
	 * @throws IOException 文件IO异常
	 */
	protected void saveAsJpeg(BufferedImage image, File output) throws IOException {
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
		
		ImageWriter writer = null;
		while (writers.hasNext()) {
			ImageWriter w = writers.next();
			if (w.getClass().getName().startsWith("com.twelvemonkeys")) {
				writer = w;
				break;
			}
		}
		if (writer == null) {
			writers = ImageIO.getImageWritersByFormatName("jpeg");
			while (writers.hasNext()) {
				ImageWriter w = writers.next();
				if (w.getClass().getName().startsWith("com.sun.imageio")) {
					writer = w;
					break;
				}
			}
		}
		if (writer != null) {
			MyLogUtil.info(FileUploadService.class, "当前使用的 JPEG writer: " + writer.getClass().getName());
		} else {
			MyLogUtil.info(FileUploadService.class, "没有找到合适的 JPEG writer!使用默认兜底处理!");
			ImageIO.write(image, "jpeg", output);
			return;
		}
		try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
			writer.setOutput(ios);
			JPEGImageWriteParam param = new JPEGImageWriteParam(null);
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.8f);
			writer.write(null, new IIOImage(image, null, null), param);
			ios.flush();
			BufferedImage read = ImageIO.read(output);
			if (read == null) {
				// 删除无效文件
				if (output.exists() && !output.delete()) {
					MyLogUtil.error(FileUploadService.class, "无法删除无效图片文件: " + output);
				}
				throw new OperationException("图片保存后验证失败：无法读取保存的图片");
			}
		} finally {
			writer.dispose();
		}
	}
	
	/**
	 * 文件名消毒
	 */
	protected String sanitizeFileName(String fileName, FileCategory fileType) {
		if (MyStringUtil.isNull(fileName)) return fileName;
		
		// 对于RAR分卷文件，只进行基本安全处理，保留完整文件名结构
		if (fileType == FileCategory.ARCHIVE && isRarMultiPartFile(fileName))
			return fileName.replaceAll("\\.\\./", "_")
					.replaceAll("\\.\\.\\\\", "_")
					.replaceAll("[\\\\/:*?\"<>|]", "_");
		// 其他文件使用破坏消毒逻辑
		return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
	}
	
	/**
	 * 生成文件名
	 */
	protected String generateFileName(MultipartFile file, FileCategory fileType) throws IOException {
		String timestamp = TimeUtil.getNowTime();
		if (fileType == FileCategory.IMG) {
			return timestamp + ".jpeg";
		} else if (fileType == FileCategory.ARCHIVE) {
			String archiveType = detectArchiveType(file);
			if (archiveType == null) {
				throw new OperationException("不支持的压缩包类型");
			}
			return timestamp + "." + archiveType;
		}
		return timestamp;
	}
}
