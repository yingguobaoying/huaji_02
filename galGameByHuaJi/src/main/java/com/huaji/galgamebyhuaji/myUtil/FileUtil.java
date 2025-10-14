package com.huaji.galgamebyhuaji.myUtil;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
	private FileUtil() {}
	
	public static String toRelativeUrl(String absolutePath, FileCategory fileCategory) {
		if (MyStringUtil.isNull(absolutePath))
			return "default.jpeg"; // 默认图片
		
		// 如果是网络URL，直接返回
		if (absolutePath.startsWith("http://") || absolutePath.startsWith("https://"))
			return absolutePath;
		//这里存放的是绝对路径到static处截止(xxx/static)
		if (fileCategory == null)
			fileCategory = FileCategory.OTHER;
		String resourceSavePath = formatUrl(Constant.getRESOURCE_SAVE_PATH(), fileCategory.getFILE_SAVE_URL());
		absolutePath = formatUrl(absolutePath);
		// 转换为相对路径
		String relativePath = convertToRelativePath(absolutePath, resourceSavePath);
		// 确保不以斜杠开头
		while (relativePath.startsWith("/")) relativePath = relativePath.substring(1);
		// 直接返回相对路径,不能以/开头
		return relativePath;
	}
	
	/**
	 * 将绝对路径转换为相对于资源保存路径的相对路径
	 */
	private static String convertToRelativePath(String absolutePath, String basePath) {
		// 如果绝对路径已经包含basePath，直接提取相对部分
		if (absolutePath.contains(basePath)) {
			int index = absolutePath.indexOf(basePath) + basePath.length();
			String relative = absolutePath.substring(index);
			// 移除开头的路径分隔符
			return removeLeadingSeparators(relative);
		}
		// 尝试从路径中提取static之后的部分
		return extractStaticRelativePath(absolutePath);
		
	}
	
	/**
	 * 从路径中提取static目录之后的相对路径
	 */
	private static String extractStaticRelativePath(String absolutePath) {
		// 查找static目录
		int staticIndex = absolutePath.indexOf("static");
		if (staticIndex != -1) {
			String relative = absolutePath.substring(staticIndex + "static".length());
			return removeLeadingSeparators(relative.replace(File.separatorChar, '/'));
		}
		// 查找不到时直接返回文件名
		return new File(absolutePath).getName();
	}
	
	/**
	 * 移除路径开头的分隔符
	 */
	private static String removeLeadingSeparators(String path) {
		if (MyStringUtil.isNull(path)) {
			return path;
		}
		
		String result = path;
		while (result.startsWith("/") || result.startsWith("\\")) {
			result = result.substring(1);
		}
		return result;
	}
	
	public static String formatUrl(String url) {
		if (MyStringUtil.isNull(url)) {
			return "";
		}
		// 移除空格，统一使用正斜杠，移除多余的分隔符
		return url.replace(" ", "")
				.replace("\\", "/")
				.replaceAll("/+", "/");
	}
	
	public static String formatUrl(String url, String... sumPath) {
		if (MyStringUtil.isNull(url)) {
			url = "";
		}
		if (sumPath == null)
			return url;
		StringBuilder sb = new StringBuilder(url.replace(" ", "").replace("\\", "/"));
		for (String path : sumPath) {
			if (!MyStringUtil.isNull(path)) {
				String cleanPath = path.replace(" ", "").replace("\\", "/");
				if (!sb.isEmpty() && !sb.toString().endsWith("/") && !cleanPath.startsWith("/")) {
					sb.append("/");
				}
				sb.append(cleanPath);
			}
		}
		// 如果是网络URL，直接返回
		if (url.startsWith("http://") || url.startsWith("https://"))
			return url;
		return sb.toString().replaceAll("/+", "/");
	}
	
	/**
	 * 判断路径是否为绝对路径（跨平台）
	 */
	public static boolean isAbsolutePath(String pathString) {
		if (pathString == null || pathString.trim().isEmpty()) {
			return false;
		}
		try {
			Path path = Paths.get(pathString);
			return path.isAbsolute();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 格式化文件大小，自动转换为合适的单位(B/KB/MB/GB)
	 *
	 * @param size 文件大小(字节)
	 *
	 * @return 格式化后的文件大小字符串
	 */
	public static String formatFileSize(long size) {
		if (size < 1024) return size + "B";
		else if (size < 1024 * 1024) return String.format("%.2fKB", size / 1024.0);
		else if (size < 1024L * 1024 * 1024) return String.format("%.2fMB", size / (1024.0 * 1024));
		else return String.format("%.2fGB", size / (1024.0 * 1024 * 1024));
	}
}
