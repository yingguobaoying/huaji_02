package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.FileUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//因为这个应该不支持背景图添加功能所以这里就直接写了
@RestController
public class BackgroundImgController {
	@GetMapping("/api/getImgList")
	public ReturnResult<String> getBackgroundImg() {
		try {
			String[] fileNameList = getFileNameList();
			if (fileNameList == null || fileNameList.length == 0) {
				updateFileNameList(
						Paths.get(Constant.getRESOURCE_SAVE_PATH(), FileCategory.IMG.getFILE_SAVE_URL(), "background").toFile()
				);
				fileNameList = getFileNameList();
			}
			if (fileNameList == null || fileNameList.length == 0)
				return ReturnResult.isFalse("没有任何背景图片捏~");
			List<String> fileName = new ArrayList<>(10);
			List<String> tempList = Arrays.asList(fileNameList);
			Collections.shuffle(tempList);
			fileName.addAll(tempList.subList(0, Math.min(10, tempList.size())));
			return ReturnResult.isTrue("背景图片获取成功!", fileName, -1);
		} catch (Exception e) {
			MyLogUtil.error(getClass(), e);
			return ReturnResult.isFalse("没有任何背景图片捏~");
		}
	}
	
	private static String[] fileNameList = null;
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private static String[] getFileNameList() {
		try {
			lock.readLock().lock();
			return fileNameList;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	//每小时自动刷新一次
	@Scheduled(fixedRate = 60 * 60 * 1000)
	public void update() {
		updateFileNameList(
				Paths.get(Constant.getRESOURCE_SAVE_PATH(), FileCategory.IMG.getFILE_SAVE_URL(), "background").toFile()
		);
	}
	
	public static void updateFileNameList(File URL) {
		try {
			lock.writeLock().lock();
			if (URL == null)
				return;
			if (!URL.exists()) {
				URL.mkdirs();
				return;
			}
			if (URL.isDirectory()) {
				File[] files = URL.listFiles(File::isFile);
				if (files == null || files.length == 0)
					return;
				fileNameList = new String[files.length];
				for (int i = 0; i < files.length; i++)
					fileNameList[i] = FileUtil.toRelativeUrl(files[i].getPath(), FileCategory.IMG);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}
}
