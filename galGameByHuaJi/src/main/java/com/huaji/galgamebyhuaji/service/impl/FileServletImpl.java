package com.huaji.galgamebyhuaji.service.impl;


import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.dao.DownloadMapper;
import com.huaji.galgamebyhuaji.dao.ResourcesMapper;
import com.huaji.galgamebyhuaji.entity.Download;
import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.FileServlet;
import com.huaji.galgamebyhuaji.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.huaji.galgamebyhuaji.constant.Constant.*;

/**
 * 文件服务实现类，处理文件上传、下载、删除等操作 支持图片压缩和压缩包文件处理
 *
 * @author 滑稽/因果报应
 */
@Service
public class FileServletImpl implements FileServlet {
	
	//获取的为:servletContext.getRealPath("/static/");
	private String basePath = Constant.RESOURCE_SAVE_PATH;
	
	@Autowired
	LoginService loginService;
	@Autowired
	DownloadMapper downloadMapper;
	@Autowired
	ResourcesMapper resourcesMapper;
	private static final String[] PROTECTED_NAME = new String[]{
			"default.jpeg", "error.jpeg", "ZhenZhanTu.jpg",
			"default", "error", "ZhenZhanTu"
	};
	
	private static boolean isProtected (String fileName) {
		for ( String s : PROTECTED_NAME ) {
			if ( fileName.equals(s) ) {
				return true;
			}
		}
		return false;
	}
	
	private static final Map<byte[], String> SUPPORTED_ARCHIVES = Map.ofEntries(
			Map.entry(new byte[]{0x50, 0x4B, 0x03, 0x04}, "zip"),
			Map.entry(new byte[]{0x52, 0x61, 0x72, 0x21}, "rar"),
			Map.entry(new byte[]{0x37, 0x7A, (byte) 0xBC}, "7z"),
			Map.entry(new byte[]{0x04, 0x22, 0x4D, 0x18}, "lz4"),
			Map.entry(new byte[]{0x1F, (byte) 0x8B}, "gz") // 修正：gzip
	                                                                           );
	
	private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
			"image/jpeg", "image/png");
	
	/**
	 * 上传文件处理方法 1. 验证用户登录状态 2. 检查文件是否为空 3. 根据文件类型验证文件大小 4. 生成文件名 5. 根据文件类型调用相应的保存方法
	 *
	 * @param file
	 * 		上传的文件
	 * @param fileType
	 * 		文件类型(jpeg/zip)
	 * @param usersToken
	 * 		用户token
	 * @param name
	 * 		自定义文件名(可选)
	 *
	 * @return 返回操作结果
	 *
	 * @throws IOException
	 * 		文件IO异常
	 * @throws BestException
	 * 		业务异常
	 * @throws OperationException
	 * 		操作异常
	 */
	@Override
	public ReturnResult<String> uploadFile (MultipartFile file, String fileType, String usersToken, String name)
			throws IOException, BestException, OperationException {
		if ( MyStringUtil.isNull(basePath) )
			basePath = Constant.RESOURCE_SAVE_PATH;
		Users users = loginService.testLogin(usersToken);
		if ( file.isEmpty() )
			throw new OperationException("上传文件为空");
		
		try {
			if ( "jpeg".equalsIgnoreCase(fileType) ) {
				validateFileSize(file, JPEG_MIN, JPEG_MAX);
			} else if ( "zip".equalsIgnoreCase(fileType) ) {
				validateFileSize(file, RAT_MIN, RAR_MAX);
			} else throw new OperationException("不支持的文件类型!当前仅支持:\n图片: jpeg \n 压缩包: zip");
		} catch ( OperationException e ) {
			throw new OperationException(String.format(
					"上传文件大小不符合要求!图片文件大小范围:%.2fKB-%.2fMB,压缩包大小范围:%.2fMB-%.2fGB",
					JPEG_MIN / 1024.0,
					JPEG_MAX / (1024.0 * 1024),
					RAT_MIN / (1024.0 * 1024),
					RAR_MAX / (1024.0 * 1024 * 1024)
			                                          ));
		}
		
		String fileName;
		if ( MyStringUtil.isNull(name) ) {
			String timestamp = TimeUtil.getNowTime();
			String userId = String.valueOf(users.getUserId());
			fileName = userId + timestamp;
		} else {
			fileName = name.replaceAll("[\\\\/:*?\"<>|]", "_");
		}
		
		Path resolvedPath = Paths.get(basePath, fileName).normalize();
		if ( !resolvedPath.startsWith(basePath) ) {
			throw new OperationException("非法路径");
		}
		
		return switch ( fileType.toLowerCase() ) {
			case "jpeg" -> {
				fileName += ".jpeg";
				yield saveImage(file, fileName);
			}
			case "zip" -> {
				String archiveType = detectArchiveType(file);
				if ( archiveType == null ) {
					throw new OperationException("不支持的文件类型");
				}
				yield saveArchive(file, fileName + "." + archiveType); // ✅ 只拼一次
			}
			default -> throw new OperationException("不支持的文件类型: " + fileType);
		};
	}
	
	/**
	 * 文件下载方法 1. 验证用户登录状态 2. 检查资源是否存在 3. 设置HTTP响应头 4. 记录下载日志 5. 返回文件流
	 *
	 * @param fileName
	 * 		文件名
	 * @param downName
	 * 		下载时显示的文件名
	 * @param usersToken
	 * 		用户token
	 * @param rId
	 * 		资源ID
	 *
	 * @return 包含文件流的响应实体
	 *
	 * @throws IOException
	 * 		文件IO异常
	 * @throws BestException
	 * 		业务异常
	 * @throws OperationException
	 * 		操作异常
	 */
	@Override
	public ReturnResult<ResponseEntity<InputStreamResource>> dowFile (String fileName, String downName, String usersToken, Integer rId)
			throws IOException, BestException, OperationException {
		if ( MyStringUtil.isNull(basePath) )
			basePath = Constant.RESOURCE_SAVE_PATH;
		Users users = loginService.testLogin(usersToken);
		Resources resources = resourcesMapper.selectByPrimaryKey(rId);
		if ( resources == null || resources.getrId() == null ) {
			throw new OperationException("错误!不存在的该资源!");
		}
		
		File file = Paths.get(basePath, fileName).toFile();
		if ( !file.exists() ) {
			throw new OperationException("下载失败!因为指定的文件" + fileName + "不存在!");
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData(downName, file.getName());
		
		Download download = new Download();
		download.setrId(rId);
		download.setTime(new Date());
		download.setUserId(users.getUserId());
		download.setType("服务器下载");
		int insert = downloadMapper.insert(download);
		if ( insert != 1 ) throw new WriteError(1, insert);
		
		InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));
		ResponseEntity<InputStreamResource> entity = ResponseEntity.ok()
				.headers(headers)
				.contentLength(file.length())
				.body(resource);
		MyLogUtil.info(FileServletImpl.class, "用户[ID={%s}]下载文件[{%s}]，大小={%s}".formatted(
				users.getUserId(), fileName, formatFileSize(file.length())
		                                                                                       ));
		return new ReturnResult<ResponseEntity<InputStreamResource>>().operationTrue("下载请求成功!", entity);
	}
	
	/**
	 * 验证文件大小是否在指定范围内
	 *
	 * @param file
	 * 		要验证的文件
	 * @param min
	 * 		最小文件大小(字节)
	 * @param max
	 * 		最大文件大小(字节)
	 *
	 * @throws OperationException
	 * 		当文件大小不在指定范围内时抛出
	 */
	private void validateFileSize (MultipartFile file, long min, long max) {
		if ( file.getSize() < min || file.getSize() > max ) {
			throw new OperationException("错误!文件大小不符合要求: 上传范围为%s - %s,您提供的文件为:%s".formatted(
					formatFileSize(min), // 转换为 KB
					formatFileSize(max), // 转换为 MB
					formatFileSize(file.getSize())
			                                                                                                     ));
		}
	}
	
	/**
	 * 检查字节数组是否以指定的魔数开头 用于文件类型识别
	 *
	 * @param fileHeader
	 * 		文件头字节数组
	 * @param magicBytes
	 * 		要匹配的魔数字节数组
	 *
	 * @return 如果匹配返回true，否则返回false
	 */
	private boolean startsWith (byte[] fileHeader, byte[] magicBytes) {
		if ( fileHeader.length < magicBytes.length ) {
			return false;
		}
		for ( int i = 0; i < magicBytes.length; i++ ) {
			if ( fileHeader[ i ] != magicBytes[ i ] ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 保存压缩包文件 1. 创建目标目录 2. 保存文件到指定位置 3. 错误处理：删除不完整的文件
	 *
	 * @param file
	 * 		要保存的文件
	 * @param fileName
	 * 		目标文件名
	 *
	 * @return 保存结果
	 *
	 * @throws IOException
	 * 		文件IO异常
	 */
	private ReturnResult<String> saveArchive (MultipartFile file, String fileName) throws IOException {
		Path targetDir = getOrCreateDirectory("rar").toPath();
		if ( !targetDir.startsWith(basePath) ) {
			throw new IOException("非法路径");
		}
		Files.createDirectories(targetDir);
		
		Path targetFile = targetDir.resolve(fileName);
		try {
			file.transferTo(targetFile); // 直接保存
		} catch ( Exception e ) {
			
			try {// 删除可能写了一半的文件
				Files.deleteIfExists(targetFile);
			} catch ( IOException ex ) {
				MyLogUtil.error(FileServletImpl.class, "清理传输失败残留文件失败,目标文件: " + targetFile);
				MyLogUtil.error(FileServletImpl.class, ex);
			}
			MyLogUtil.error(FileServletImpl.class, "存储文件时发生错误:" + e.getMessage());
			MyLogUtil.error(FileServletImpl.class, e);
			throw new OperationException("文件上传失败!原因是: " + e.getMessage());
		}
		
		MyLogUtil.info(FileServletImpl.class, String.format(
				"压缩文件保存成功：文件名：%s，大小：%d bytes",
				targetFile.getFileName(), Files.size(targetFile)
		                                                   ));
		return new ReturnResult<String>().operationTrue("文件保存成功", targetFile.getFileName().toString());
	}
	
	/**
	 * 检测压缩包类型 通过读取文件头部的魔数来判断文件类型
	 *
	 * @param file
	 * 		要检测的文件
	 *
	 * @return 文件类型字符串，如"zip"、"rar"等，如果不支持则返回null
	 *
	 * @throws IOException
	 * 		文件读取异常
	 */
	private String detectArchiveType (MultipartFile file) throws IOException {
		try ( InputStream is = file.getInputStream() ) {
			byte[] header = new byte[ 10 ];
			is.read(header);
			for ( Map.Entry<byte[], String> entry : SUPPORTED_ARCHIVES.entrySet() ) {
				if ( startsWith(header, entry.getKey()) ) {
					return entry.getValue();
				}
			}
		}
		return null;
	}
	
	/**
	 * 格式化文件大小，自动转换为合适的单位(B/KB/MB/GB)
	 *
	 * @param size
	 * 		文件大小(字节)
	 *
	 * @return 格式化后的文件大小字符串
	 */
	private String formatFileSize (long size) {
		if ( size < 1024 ) return size + "B";
		else if ( size < 1024 * 1024 ) return String.format("%.2fKB", size / 1024.0);
		else if ( size < 1024L * 1024 * 1024 ) return String.format("%.2fMB", size / (1024.0 * 1024));
		else return String.format("%.2fGB", size / (1024.0 * 1024 * 1024));
	}
	
	/**
	 * 保存图片文件 1. 验证图片格式 2. 转换为JPEG格式 3. 保存图片 4. 记录压缩信息
	 *
	 * @param image
	 * 		图片文件
	 * @param fileName
	 * 		目标文件名
	 *
	 * @return 保存结果
	 *
	 * @throws IOException
	 * 		文件IO异常
	 */
	private ReturnResult<String> saveImage (MultipartFile image, String fileName) throws IOException {
		File targetDir = getOrCreateDirectory("img");
		File targetFile = new File(targetDir, fileName);
		
		if ( !ALLOWED_IMAGE_TYPES.contains(image.getContentType()) ) {
			throw new OperationException("不支持的图片格式");
		}
		try {
			BufferedImage sourceImage = ImageIO.read(image.getInputStream());
			if ( sourceImage == null ) {
				throw new OperationException("无法解析图片文件");
			}
			
			BufferedImage finalImage = convertToJpeg(sourceImage);
			
			saveAsJpeg(finalImage, targetFile);
			
			long originalSize = image.getSize();
			long compressedSize = targetFile.length();
			double compressionRatio = (double) (originalSize - compressedSize) / originalSize * 100;
			
			MyLogUtil.info(FileServletImpl.class, String.format(
					"图片压缩成功：原始大小：%d bytes，压缩后大小：%d bytes，压缩率：%.2f%%，位置：%s",
					originalSize, compressedSize, compressionRatio, targetFile
			                                                   ));
			
			return new ReturnResult<String>().operationTrue("图片保存成功", fileName);
			
		} catch ( Exception e ) {
			if ( targetFile.exists() && !targetFile.delete() ) {
				MyLogUtil.error(FileServletImpl.class, "无法删除临时文件: " + targetFile);
			}
			MyLogUtil.error(FileServletImpl.class, e);
			throw new OperationException("图片处理失败: " + e.getMessage());
		}
	}
	
	/**
	 * 将图片转换为JPEG格式 创建一个新的RGB图片，并将原图绘制到新图片上
	 *
	 * @param source
	 * 		源图片
	 *
	 * @return 转换后的JPEG图片
	 */
	private BufferedImage convertToJpeg (BufferedImage source) {
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
	
	/**
	 * 获取或创建目录 如果目录不存在则创建
	 *
	 * @param subPath
	 * 		子目录路径
	 *
	 * @return 目录文件对象
	 *
	 * @throws IOException
	 * 		目录创建失败时抛出
	 */
	private File getOrCreateDirectory (String subPath) throws IOException {
		File dir = new File(basePath, subPath);
		if ( !dir.exists() && !dir.mkdirs() ) {
			throw new IOException("无法创建目录: " + dir.getAbsolutePath());
		}
		return dir;
	}
	
	/**
	 * 删除文件 1. 验证路径合法性 2. 检查文件是否存在 3. 执行删除操作
	 *
	 * @param fileName
	 * 		文件名
	 * @param fileUrl
	 * 		文件URL
	 *
	 * @return 删除结果
	 */
	@Override
	public ReturnResult<String> deleteFile (String fileName, String fileUrl) {
		if ( MyStringUtil.isNull(basePath) )
			basePath = Constant.RESOURCE_SAVE_PATH;
		if ( MyStringUtil.isNull(fileUrl) ) {
			fileUrl = basePath;
		}
		File target = Paths.get(fileUrl, fileName).normalize().toFile();
		if ( !target.getAbsolutePath().startsWith(basePath) ) {
			throw new OperationException("非法路径");
		}
		
		if ( !target.exists() ) {
			throw new OperationException("文件不存在");
		}
		if ( !target.isFile() ) {
			throw new OperationException("目标不是文件");
		}
		if ( isProtected(fileName) ) {// 保护文件,这里以成功删除作为返回,方便其他地方调用时保证逻辑的连贯
			return ReturnResult.isTrue("文件为保护资源!无法删除!请将状态设置为软删除", null);
		}
		if ( !target.delete() ) {
			throw new OperationException("文件删除失败");
		}
		
		return ReturnResult.isTrue("文件删除成功", null);
	}
	
	/**
	 * 将图片保存为JPEG格式 1. 获取JPEG图片写入器 2. 设置压缩参数 3. 写入图片数据
	 *
	 * @param image
	 * 		要保存的图片
	 * @param output
	 * 		输出文件
	 *
	 * @throws IOException
	 * 		文件IO异常
	 */
	private void saveAsJpeg (BufferedImage image, File output) throws IOException {
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
		
		ImageWriter writer = null;
		while ( writers.hasNext() ) {
			ImageWriter w = writers.next();
			if ( w.getClass().getName().startsWith("com.twelvemonkeys") ) {
				writer = w;
				break;
			}
		}
		if ( writer == null ) {
			writers = ImageIO.getImageWritersByFormatName("jpeg");
			while ( writers.hasNext() ) {
				ImageWriter w = writers.next();
				if ( w.getClass().getName().startsWith("com.sun.imageio") ) {
					writer = w;
					break;
				}
			}
		}
		if ( writer != null ) {
			MyLogUtil.info(FileServletImpl.class, "当前使用的 JPEG writer: " + writer.getClass().getName());
		} else {
			MyLogUtil.info(FileServletImpl.class, "没有找到合适的 JPEG writer!使用默认兜底处理!");
			ImageIO.write(image, "jpeg", output);
			return;
		}
		
		try ( ImageOutputStream ios = ImageIO.createImageOutputStream(output) ) {
			writer.setOutput(ios);
			JPEGImageWriteParam param = new JPEGImageWriteParam(null);
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.8f);
			writer.write(null, new IIOImage(image, null, null), param);
		} finally {
			writer.dispose();
		}
	}
}