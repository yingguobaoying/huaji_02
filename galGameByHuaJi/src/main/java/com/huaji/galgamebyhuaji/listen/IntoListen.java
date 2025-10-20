package com.huaji.galgamebyhuaji.listen;


import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.*;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Random;

import static com.huaji.galgamebyhuaji.constant.Constant.CONSTANT_PASSWORD;

@Component
@DependsOn({"vaultConfigValidator", "JWTConfig"})
@RequiredArgsConstructor
public class IntoListen {
	
	private final ResourcesService resourcesService;
	private final TagService tagService;
	private final SessionService sessionService;
	private final UserMxgServlet userMxgServlet;
	final
	ServletContext servletContext;
	final
	PasswordEncryptionUtil passwordEncryptionUtil;
	@Value("${resource-save-path}")
	private String resourceSavePath;
	final
	RedisMemoryService redisMemoryService;
	final RootServlet rootServlet;
	
	
	@EventListener
	public void onContextRefreshed(ContextRefreshedEvent event) throws Exception {
		if (event.getApplicationContext().getParent() == null) {
			redisMemoryService.delAllData();//清空旧数据
			rootServlet.rootUserInit();
			tagService.getTagMap();
			resourcesService.getAllResources();
			userMxgServlet.getAllUserListMxg();
			//设置防止时序攻击的固定密码,不过大部分情况下密码不会包括中文所以这里夹带了点私货
			CONSTANT_PASSWORD = passwordEncryptionUtil.hashPassword("红豆可爱滴捏_Vigna_very_loveliness");
			System.out.println(resourceSavePath);
			File dir = new File(resourceSavePath);
			if (!dir.exists()) {
				if (dir.mkdirs())
					MyLogUtil.info(IntoListen.class,
					               "静态资源存储文件夹不存在!进行创建!创建位置为:" + resourceSavePath);
				else
					throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + resourceSavePath);
			}
			File imgFile = new File(resourceSavePath + File.separator + FileCategory.IMG.getFILE_SAVE_URL());
			File rarFile = new File(resourceSavePath + File.separator + FileCategory.ARCHIVE.getFILE_SAVE_URL());
			if (!imgFile.exists()) {
				if (imgFile.mkdirs())
					MyLogUtil.info(IntoListen.class,
					               "静态资源存储文件夹不存在!进行创建!创建位置为:" + imgFile);
				else
					throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + imgFile);
			}
			if (!rarFile.exists()) {
				if (rarFile.mkdirs())
					MyLogUtil.info(IntoListen.class,
					               "静态资源存储文件夹不存在!进行创建!创建位置为:" + rarFile);
				else
					throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + rarFile);
			}
			// 使用反射修改静态常量字段
			Constant.setRESOURCE_SAVE_PATH(resourceSavePath);
		}
		System.out.println("=========================================");
		System.out.println("===========滑稽/因果报应的个人小站===========");
		System.out.println("=========================================");
		
		
		System.out.println("=========================================");
		System.out.println("==============此为后端部分=================");
		System.out.println("=========================================");
		Random random = new Random();
		int i = random.nextInt(0, 500);
		if (i == 430 || i == 43 || i == 4 || i == 3) {
			MyLogUtil.info(IntoListen.class, "红豆可爱滴捏~~~~");
		}
	}
	
	
	@EventListener
	public void onContextClosed(ContextClosedEvent event) {
		int i = sessionService.manbaOut();
		MyLogUtil.info(ContextClosedEvent.class, TimeUtil.getSimpleDateFormatTime(new Date()) + "服务器关闭");
		MyLogUtil.info(ContextClosedEvent.class, "在服务器关闭时,使" + i + "位在线用户离线");
		System.err.println("在服务器关闭时,牢大肘击了" + i + "个倒霉用户");
	}
}
