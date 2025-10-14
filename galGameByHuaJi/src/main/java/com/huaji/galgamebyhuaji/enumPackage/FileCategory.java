package com.huaji.galgamebyhuaji.enumPackage;

import lombok.Getter;

import java.util.List;

@Getter
public enum FileCategory {
	ARCHIVE("rar", List.of("zip", "rar"), 2),
	IMG("img", List.of("jpeg", "png"), 1),
	OTHER(null, List.of(), 4),
	SCRIPT("ts", List.of("js", "ts"), 3);
	/**
	 * 文件类型
	 */
	private final List<String> FILE_TYPE;
	/**
	 * 文件存储的相对路径
	 */
	private final String FILE_SAVE_URL;
	private final int TYPE_NUM;
	
	FileCategory(String FILE_SAVE_URL, List<String> FILE_TYPE, int TYPE_NUM) {
		this.FILE_TYPE = FILE_TYPE;
		this.FILE_SAVE_URL = FILE_SAVE_URL;
		this.TYPE_NUM = TYPE_NUM;
	}
	
	public static String getByType(String type) {
		for (FileCategory f : FileCategory.values()) {
			for (String s : f.getFILE_TYPE()) {
				if (s.equals(type)) {
					return f.getFILE_SAVE_URL();
				}
			}
		}
		return null;
	}
}
