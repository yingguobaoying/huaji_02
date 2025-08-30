package com.huaji.galgamebyhuaji.listen;


import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.*;
import jakarta.servlet.ServletContext;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;

import static com.huaji.galgamebyhuaji.constant.Constant.CONSTANT_PASSWORD;
import static com.huaji.galgamebyhuaji.constant.Constant.RESOURCE_SAVE_PATH;

@Component
public class IntoListen {
	
	@Autowired
	private ResourcesService resourcesService;
	@Autowired
	private TagService tagService;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private UserMxgServlet userMxgServlet;
	@Autowired
	ServletContext servletContext;
	@Autowired
	@Qualifier("RNameBloomFilter")
	RBloomFilter<String> RNameBloomFilter;
	@Autowired
	@Qualifier("manufacturerFilterBloomFilter")
	RBloomFilter<String> manufacturerFilterBloomFilter;
	@Autowired
	PasswordEncryptionUtil passwordEncryptionUtil;
	@Value("${resource-save-path}")
	private String resourceSavePath;
	@Autowired
	RedisMemoryService redisMemoryService;
	
	@EventListener
	public void onContextRefreshed (ContextRefreshedEvent event) {
		if ( event.getApplicationContext().getParent() == null ) {
			redisMemoryService.delAllData();//清空旧数据
			tagService.getTagMap();
			List<Resources> allResources = resourcesService.getAllResources();
			userMxgServlet.getAllUserListMxg();
			//将资源加入过滤器
			for ( Resources resource : allResources ) {
				RNameBloomFilter.add(resource.getrName());
				manufacturerFilterBloomFilter.add(resource.getrManufacturer());
			}
			//设置防止时序攻击的固定密码,不过大部分情况下密码不会包括中文所以这里夹带了点私货
			CONSTANT_PASSWORD = passwordEncryptionUtil.hashPassword("红豆可爱滴捏_Vigna_very_loveliness");
			System.out.println(resourceSavePath);
			File dir = new File(resourceSavePath);
			if ( !dir.exists() ) {
				if ( dir.mkdirs() )
					MyLogUtil.info(IntoListen.class, "静态资源存储文件夹不存在!进行创建!创建位置为:" + resourceSavePath);
				else
					throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + resourceSavePath);
			}
			File imgFile = new File(resourceSavePath + File.separator + "img");
			File rarFile = new File(resourceSavePath + File.separator + "rar");
			if ( !imgFile.exists() ) {
				if ( imgFile.mkdirs() )
					MyLogUtil.info(IntoListen.class, "静态资源存储文件夹不存在!进行创建!创建位置为:" + resourceSavePath + "\\img");
				else
					throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + resourceSavePath + "\\img");
			}
			if ( !rarFile.exists() ) {
				if ( rarFile.mkdirs() )
					MyLogUtil.info(IntoListen.class, "静态资源存储文件夹不存在!进行创建!创建位置为:" + resourceSavePath + "\\rar");
				else
					throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + resourceSavePath + "\\rar");
			}
			RESOURCE_SAVE_PATH = resourceSavePath;
		}
	}
	
	@EventListener
	public void onContextClosed (ContextClosedEvent event) {
		int i = sessionService.manbaOut();
		MyLogUtil.info(ContextClosedEvent.class, TimeUtil.getSimpleDateFormatTime(new Date()) + "服务器关闭");
		MyLogUtil.info(ContextClosedEvent.class, "在服务器关闭时,使" + i + "位在线用户离线");
		System.err.println("在服务器关闭时,牢大肘击了" + i + "个倒霉用户");
	}
}