package com.huaji.galgamebyhuaji.vignaAiFrame.config;

import com.huaji.galgamebyhuaji.vignaAiFrame.constant.AiConstant;
import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
import com.huaji.galgamebyhuaji.entity.AiClientConfig;
import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.filter.MyBaseAdvisor;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.AiMerchantType;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.KeyServlet;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaHttpClient;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.impl.VignaBaseClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@DependsOn({"vaultConfigValidator"})
public class VignaChatClientConfig {
    private final AiClientConfigMapper clientConfigMapper;
    public static final String API_KEY_PLACEHOLDER = "红豆可爱捏";
    private static volatile Map<Long, VignaHttpClient> configMap = Collections.emptyMap();
    private static volatile Map<String, Long> idMap = Collections.emptyMap();
    private final KeyServlet keyServlet;
    private final List<MyBaseAdvisor> allAdvisor;
    @Value("${ai.chat-record-size}")
    private int size;
    @Value("${ai.chat-token-size}")
    private int tokenSize;
    
    public static VignaHttpClient getChatClient(long id) {
        return configMap.get(id);
    }
    
    public static Long getChatClientId(String code) {
        return idMap.get(code);
    }
    
    public static boolean codeState(String code) {
        Long chatClientId = getChatClientId(code);
        if (chatClientId == null) {
            return false;
        }
        return getChatClient(chatClientId) != null;
    }
    
    public void info() {
        String banner =
                """
                \n
                =============================================================
                =                                                           =
                =     __     ___                          _                 =
                =     \\ \\   / (_) __ _ _ __   __ _       /_\\   __ _(_)      =
                =      \\ \\ / /| |/ _` | '_ \\ / _` |     //_\\\\ / _` | |      =
                =       \\ V / | | (_| | | | | (_| |    /  _  \\ (_| | |      =
                =        \\_/  |_|\\__, |_| |_|\\__,_|    \\_/ \\_/\\__,_|_|      =
                =                |___/                                      =
                =                                                           =
                =============================================================""";
        System.out.println(banner);
        System.out.println(" :: Vigna-ai ::                (v0.0.1)");
        log.info("*****************************************************************");
        log.info("********************** 开始加载AI配置项 ****************************");
        log.info("********************** 当前vigna-ai对话框架版本: v0.0.1 ************");
        log.info("*****************************************************************");
        AiClientConfigExample configExample = new AiClientConfigExample();
        configExample.createCriteria().andIsActiveEqualTo(true);
        List<AiClientConfigWithBLOBs> aiClientConfigs = clientConfigMapper.selectByExampleWithBLOBs(configExample);
        if (ListUtil.isNull(aiClientConfigs)) {
            log.info("********************** 未检查到启用项目, AI配置检测完成 *********");
            return;
        }
        log.info("********************** 取得启用AI配置项共: {} 项 ****************", aiClientConfigs.size());
        Map<Long, VignaHttpClient> map = new HashMap<>(50);
        Map<String, Long> idmap = new HashMap<>(50);
        if (!ListUtil.isNull(allAdvisor)) allAdvisor.sort(Comparator.comparingInt(MyBaseAdvisor::getIndex));
        for (AiClientConfigWithBLOBs config : aiClientConfigs) {
            Long id = config.getId();
            String code = config.getCode();
            AiMerchantType type = AiMerchantType.getByTypeNum(config.getMerchant());
            VignaHttpClient client = VignaBaseClient.getInstance(config, allAdvisor, false, type);
            client.setKeyServlet(keyServlet);//密钥授权
            map.put(id, client);
            idmap.put(code, id);
        }
        idMap = idmap;
        configMap = map;
    }
    
    public void refresh() {
        log.info("----------------------------------------------------------------------");
        log.info("---------------------- 重新加载AI配置项 ------------------------------");
        log.info("----------------------------------------------------------------------");
        info();
        selfInspection();
        log.info("---------------------- 重新加载AI配置项完成 --------------------------");
        log.info("----------------------------------------------------------------------");
        log.info("---------------- 当前自动压缩上下文对话消息数量: {} (每次HTTP请求发送/接收响应均计1次) ----------------",
                 size / 2.0);
        log.info("---------------- 当前自动压缩上下文消息文本字数: {} ----------------",
                 tokenSize);
        log.info("----------------------------------------------------------------------");
    }
    
    public void selfInspection() {
        if (AiConstant.CODE_LIST.isEmpty())
            log.warn("警告:当前未定义任何系统ai功能");
        else {
            AiClientConfigExample example = new AiClientConfigExample();
            example.createCriteria().andCodeIn(AiConstant.CODE_LIST);
            List<AiClientConfig> aiClientConfigs = clientConfigMapper.selectByExample(example);
            if (aiClientConfigs.isEmpty()) {
                log.warn("警告:当前无任何系统ai实现");
                return;
            }
            if (aiClientConfigs.size() != AiConstant.CODE_LIST.size())
                log.warn("警告:实际配置的部分和预期不一致,预期配置数量{},实际为:{}", AiConstant.CODE_LIST.size(), aiClientConfigs.size());
            HashMap<String, String> codes = new HashMap<>(AiConstant.CODE_LIST.size());
            for (AiClientConfig config : aiClientConfigs) {
                VignaHttpClient chatClient = getChatClient(config.getId());
                if (chatClient == null)
                    log.warn("警告:配置{}初始化失败!", config.getCode());
                codes.put(config.getCode(), chatClient == null ? "失败" : "成功");
            }
            log.info("初始化完成检查,检查结果如下:");
            if (codes.size() == AiConstant.CODE_LIST.size()) {
                codes.forEach((k, v) -> log.info("默认配置代码:{},初始化结果:{}", k, v));
            } else {
                for (String s : AiConstant.CODE_LIST) {
                    String r = codes.get(s);
                    if (MyStringUtil.isNull(r))
                        log.info("配置{}尚未配置", s);
                    else
                        log.info("默认配置代码:{},初始化结果:{}", s, r);
                }
            }
        }
    }
}
