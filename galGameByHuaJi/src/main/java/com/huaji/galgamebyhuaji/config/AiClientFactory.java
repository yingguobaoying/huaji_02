package com.huaji.galgamebyhuaji.config;

import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.AiEnumPackage.AiMerchantType;
import com.huaji.galgamebyhuaji.myUtil.AESEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.FileUtil;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@DependsOn({"vaultConfigValidator"})
public class AiClientFactory {
    private final AiClientConfigMapper clientConfigMapper;
    private volatile Map<Long, ChatClient> configMap = Collections.emptyMap();
    
    public ChatClient getChatClient(long id) {
        return configMap.get(id);
    }
    
    @Value("${spring.cloud.vault.kv.ai-path}")
    private String aiTokenPath;
    @Value("${spring.cloud.vault.kv.backend}")
    private String bastPath;
    private final VaultTemplate vaultTemplate;
    private final AESEncryptionUtil aesEncryptionUtil;
    
    public void aiInfo() {
        log.info("***********************开始加载AI配置项*****************************");
        AiClientConfigExample configExample = new AiClientConfigExample();
        configExample.createCriteria().andIsActiveEqualTo(true);
        List<AiClientConfigWithBLOBs> aiClientConfigs = clientConfigMapper.selectByExampleWithBLOBs(configExample);
        
        if (ListUtil.isNull(aiClientConfigs)) {
            log.info("***********************未检查到启用项目,AI配置检测完成****************************");
            return;
        }
        
        log.info("***********************取得启用AI配置项共:{}项****************************", aiClientConfigs.size());
        int ok = 0, lost = 0;
        Map<Long, ChatClient> map = new HashMap<Long, ChatClient>(50);
        for (AiClientConfigWithBLOBs config : aiClientConfigs) {
            try {
                // 处理 API Key (支持 Vault)
                String apiKey = config.getApiKey();
                if (MyStringUtil.isNull(apiKey)) {
                    log.error("*****配置{}读取失败,因为密钥信息为空***", config.getName());
                    lost++;
                    continue;
                }
                if (Boolean.TRUE.equals(config.getKeyIsVault())) {
                    if (MyStringUtil.isNull(config.getApiKey())) {
                        log.error("*****配置{}读取失败,因为路径信息为空***", config.getName());
                        lost++;
                        continue;
                    }
                    String path = FileUtil.formatUrl(bastPath, aiTokenPath, config.getApiKey()).toString();
                    try {
                        VaultResponse response = vaultTemplate.read(path);
                        if (response == null || response.getData() == null || response.getData().isEmpty()) {
                            log.error("*****配置{}读取失败,因为路径{}下为空***", config.getName(), path);
                            lost++;
                            continue;
                        }
                        Map<String, Object> data = response.getData();
                        String name = data.keySet().iterator().next();
                        if (data.size() != 1)
                            log.warn("*****配置{}读取在路径{}下发现多个密钥,随机选取一个{},\n全部密钥名称{}\n***********"
                                    , config.getName(), path, name, data.keySet());
                        apiKey = data.get(name).toString();
                        if (MyStringUtil.isNull(apiKey)) {
                            log.error("*****配置{}读取失败,因为路径{}下读取的密钥{}为空***", config.getName(), path, name);
                            lost++;
                            continue;
                            
                        }
                    } catch (Exception e) {
                        log.info("*****配置{}读取失败,失败路径{},失败原因{}***", config.getName(), path, e.getMessage());
                        lost++;
                        continue;
                    }
                } else {
                    apiKey = aesEncryptionUtil.decryptValue(config.getApiKey());
                }
                // 转换数据库参数 (0~100 -> 0.0~1.0)
                Double temperature = config.getTemperature() != null ? config.getTemperature() / 100.0 : null;
                Double topP = config.getTopP() != null ? config.getTopP() / 100.0 : null;
                Double freqPenalty = config.getFrequencyPenalty() != null ? config.getFrequencyPenalty() / 100.0 : null;
                Double presPenalty = config.getPresencePenalty() != null ? config.getPresencePenalty() / 100.0 : null;
                
                ChatModel chatModel;
                AiMerchantType merchantType = AiMerchantType.getByTypeNum(config.getMerchant());
                
                switch (merchantType) {
                    case DEEP_SEEK_CLOUD -> {
                        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                                .model(config.getModel())
                                .temperature(temperature)
                                .topP(topP)
                                .frequencyPenalty(freqPenalty)
                                .presencePenalty(presPenalty)
                                .maxTokens(config.getMaxTokens())
                                .build();
                        
                        chatModel = DeepSeekChatModel.builder()
                                .deepSeekApi(DeepSeekApi.builder()
                                                     .apiKey(apiKey)
                                                     .baseUrl(config.getBaseUrl())
                                                     .build())
                                .defaultOptions(options)
                                .build();
                    }
                    case OLLAMA, OLLAMA_CLOUD -> {
                        OllamaChatOptions options = OllamaChatOptions.builder()
                                .model(config.getModel())
                                .temperature(temperature)
                                .topP(topP)
                                .numPredict(config.getMaxTokens())
                                .build();
                        
                        chatModel = OllamaChatModel.builder()
                                .ollamaApi(OllamaApi.builder()
                                                   .baseUrl(config.getBaseUrl())
                                                   .build())
                                .defaultOptions(options)
                                .build();
                    }
                    default -> {
                        // OpenAI 及所有兼容 OpenAI 协议的模型 (如中转API)
                        OpenAiChatOptions options = OpenAiChatOptions.builder()
                                .model(config.getModel())
                                .temperature(temperature)
                                .topP(topP)
                                .frequencyPenalty(freqPenalty)
                                .presencePenalty(presPenalty)
                                .maxTokens(config.getMaxTokens())
                                .build();
                        
                        chatModel = OpenAiChatModel.builder()
                                .openAiApi(OpenAiApi.builder()
                                                   .apiKey(apiKey)
                                                   .baseUrl(config.getBaseUrl())
                                                   .build())
                                .defaultOptions(options)
                                .build();
                    }
                }
                //构建 ChatClient 并绑定默认系统提示词
                ChatClient.Builder clientBuilder = ChatClient.builder(chatModel);
                if (StringUtils.hasText(config.getContent())) {
                    clientBuilder.defaultSystem(config.getContent());
                }
                map.put(config.getId(), clientBuilder.build());
                ok++;
                log.info("成功加载AI模型: id={}, name={}, merchant={}", config.getId(), config.getName(), merchantType.getName());
            } catch (Exception e) {
                log.error("***********************AI配置文件[{}]加载失败: {}****************************",
                          config.getName(), e.getMessage(), e);
                lost++;
            }
        }
        configMap = Collections.unmodifiableMap(map);
        log.info("***********************加载成功AI配置项共:{}项, 加载失败:{}项****************************", ok, lost);
    }
    
    /**
     * 重新加载所有配置
     */
    public void refresh() {
        log.info("-----------------------重新加载AI配置项----------------------------");
        aiInfo();
        log.info("-----------------------重新加载AI配置项完成----------------------------");
    }
    
}
