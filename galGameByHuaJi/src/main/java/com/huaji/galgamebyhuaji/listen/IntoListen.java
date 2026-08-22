package com.huaji.galgamebyhuaji.listen;


import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.controller.BackgroundImgController;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.*;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Random;

import static com.huaji.galgamebyhuaji.constant.Constant.CONSTANT_PASSWORD;

/**
 * @author 滑稽/因果报应
 */
@Component
@DependsOn({"vaultConfigValidator", "JWTConfig"})
@RequiredArgsConstructor
@Slf4j
public class IntoListen {
//    private final AiClientFactory aiClientFactory;
//    private final AiHttpRecordScheduler aiHttpRecordScheduler;
    private final ResourcesService resourcesService;
    private final TagService tagService;
    private final SessionService sessionService;
    private final UserMxgServlet userMxgServlet;
    final ServletContext servletContext;
    final PasswordEncryptionUtil passwordEncryptionUtil;
    @Value("${resource-save-path}")
    private String resourceSavePath;
    final RedisMemoryService redisMemoryService;
    final RootServlet rootServlet;
    private static final Date starTime;
    
    static {
        starTime = new Date();
    }
    
    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) throws Exception {
        Date date = null;
        if (event.getApplicationContext().getParent() == null) {
            log.info("================服务器启动监听器开始启动,准备开始初始化各项数据===================");
            date = new Date();
            redisMemoryService.delAllData();//清空旧数据
            rootServlet.rootUserInit();
            tagService.getTagMap();
            resourcesService.getAllResources();
            
            //设置防止时序攻击的固定密码,不过大部分情况下密码不会包括中文所以这里夹带了点私货
            CONSTANT_PASSWORD = passwordEncryptionUtil.hashPassword("红豆可爱滴捏_Vigna_very_loveliness");
            File dir = new File(resourceSavePath);
            if (!dir.exists()) {
                if (dir.mkdirs()) log.info("静态资源存储文件夹不存在!进行创建!创建位置为:{}", resourceSavePath);
                else throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + resourceSavePath);
            }
            File imgFile = new File(resourceSavePath + File.separator + FileCategory.IMG.getFILE_SAVE_URL());
            File rarFile = new File(resourceSavePath + File.separator + FileCategory.ARCHIVE.getFILE_SAVE_URL());
            if (!imgFile.exists()) {
                if (imgFile.mkdirs()) log.info("静态资源存储文件夹不存在!进行创建!创建位置为:{}", imgFile);
                else throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + imgFile);
            }
            if (!rarFile.exists()) {
                if (rarFile.mkdirs()) log.info("静态资源存储文件夹不存在!进行创建!创建位置为:{}", rarFile);
                else throw new RuntimeException("静态资源存储文件夹创建失败!创建位置为:" + rarFile);
            }
            Constant.setRESOURCE_SAVE_PATH(resourceSavePath);
//            aiClientFactory.aiInfo();
//            aiClientFactory.selfInspection();
            System.out.println("=========================================");
            System.out.println("=========  滑稽/因果报应的个人小站  =========");
            System.out.println("=========================================");
            System.out.println("=========================================");
            System.out.println("============  此为后端部分  ===============");
            System.out.println("=========================================");
            Random random = new Random();
            int i = random.nextInt(0, 500);
            //留给后续的我的随机数选择说明:红豆于2019.4.30加入游戏,身高1.42,生日 12.12
            if (i == 430 || i == 43 || i == 4 || i == 3 || i == 12 || i == 142 || i == 14 || i == 1) {
                log.info("=========================================");
                log.info("===========  红豆可爱滴捏~~~~  ============");
                log.info("=========================================");
                System.err.println("=========================================");
                System.err.println("==============  红豆可爱滴捏~~~~  =========");
                System.err.println("=========================================");
                System.err.println("*****************************************");
                System.err.println("************  红豆可爱滴捏~~~~  ***********");
                System.err.println("*****************************************");
                System.err.println("=========================================");
                System.err.println("============  红豆可爱滴捏~~~~  ===========");
                System.err.println("=========================================");
            }
            BackgroundImgController.updateFileNameList(Paths.get(Constant.getRESOURCE_SAVE_PATH(), FileCategory.IMG.getFILE_SAVE_URL(), "background").toFile());
            log.info("服务器应用层启动完成用时:{}毫秒", (System.currentTimeMillis()) - date.getTime());
            log.info("类加载开始时间:{},全部初始化完成时间:{},用时{}毫秒", TimeUtil.getVisualDateFormatTime(starTime), TimeUtil.getVisualDateFormatTime(new Date()),
                     System.currentTimeMillis() - starTime.getTime());
        }
    }
    
    
    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        int i = sessionService.manbaOut();
        log.info("{}服务器关闭", TimeUtil.getSimpleDateFormatTime(new Date()));
        log.info("在服务器关闭时,使{}位在线用户离线", i);
        System.err.println("在服务器关闭时,牢大肘击了" + i + "个倒霉用户");
        log.error("在服务器关闭时,牢大肘击了{}个倒霉用户", i);
    }
}