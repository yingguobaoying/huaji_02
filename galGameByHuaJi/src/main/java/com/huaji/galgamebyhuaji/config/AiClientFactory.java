//package com.huaji.galgamebyhuaji.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.huaji.galgamebyhuaji.AOP.ai.MyBaseAdvisor;
//import com.huaji.galgamebyhuaji.constant.AiConstant;
//import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
//import com.huaji.galgamebyhuaji.entity.AiClientConfig;
//import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
//import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
//import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.AiMerchantType;
//import com.huaji.galgamebyhuaji.myUtil.AESEncryptionUtil;
//import com.huaji.galgamebyhuaji.myUtil.ListUtil;
//import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.deepseek.DeepSeekChatModel;
//import org.springframework.ai.deepseek.DeepSeekChatOptions;
//import org.springframework.ai.deepseek.api.DeepSeekApi;
//import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.ai.ollama.api.OllamaChatOptions;
//import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.ai.openai.OpenAiChatOptions;
//import org.springframework.ai.openai.api.OpenAiApi;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//import org.springframework.vault.core.VaultTemplate;
//import org.springframework.vault.support.VaultResponse;
//import org.springframework.web.client.RestClient;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//@DependsOn({"vaultConfigValidator"})
//public class AiClientFactory {
//    private final AiClientConfigMapper clientConfigMapper;
//    private final ObjectMapper objectMapper;
//    private static volatile Map<Long, ChatClient> configMap = Collections.emptyMap();
//    private final List<MyBaseAdvisor> allAdvisor;
//
//    public static ChatClient getChatClient(long id) {
//        return configMap.get(id);
//    }
//
//    @Value("${spring.cloud.vault.kv.ai-path}")
//    private String aiTokenPath;
//    @Value("${spring.cloud.vault.kv.backend}")
//    private String bastPath;
//    private final VaultTemplate vaultTemplate;
//    private final AESEncryptionUtil aesEncryptionUtil;
//    public static final String API_KEY_PLACEHOLDER = "红豆可爱捏";
//
//    /**
//     * 创建带 AI HTTP 拦截器的 RestClient.Builder，用于捕获原始请求/响应 JSON
//     */
//    private RestClient.Builder interceptedRestClientBuilder() {
//        return RestClient.builder()
//                .requestInterceptor(new AiHttpRecordInterceptor());
//    }
//
//    public void aiInfo() {
//        log.info("***********************开始加载AI配置项*****************************");
//        AiClientConfigExample configExample = new AiClientConfigExample();
//        configExample.createCriteria().andIsActiveEqualTo(true);
//        List<AiClientConfigWithBLOBs> aiClientConfigs = clientConfigMapper.selectByExampleWithBLOBs(configExample);
//
//        if (ListUtil.isNull(aiClientConfigs)) {
//            log.info("***********************未检查到启用项目,AI配置检测完成****************************");
//            return;
//        }
//
//        log.info("***********************取得启用AI配置项共:{}项****************************", aiClientConfigs.size());
//        int ok = 0, lost = 0;
//        Map<Long, ChatClient> map = new HashMap<>(50);
//        for (AiClientConfigWithBLOBs config : aiClientConfigs) {
//            try {
//                String apiKey = config.getApiKey();
//                if (MyStringUtil.isNull(apiKey)) {
//                    log.error("*****配置{}读取失败,因为密钥信息为空***", config.getName());
//                    lost++;
//                    continue;
//                }
//                if (API_KEY_PLACEHOLDER.equalsIgnoreCase(apiKey)) {
//                    log.error("配置错误!配置{}的API_KEY为占位符!已经跳过此不安全的配置!", config.getId());
//                    lost++;
//                    continue;
//                }
//                if (Boolean.TRUE.equals(config.getKeyIsVault())) {
//                    if (MyStringUtil.isNull(config.getApiKey())) {
//                        log.error("*****配置{}读取失败,因为路径信息为空***", config.getName());
//                        lost++;
//                        continue;
//                    }
//                    String path = bastPath + "/data/" + aiTokenPath + "/" + config.getApiKey();
//                    try {
//                        VaultResponse response = vaultTemplate.read(path);
//                        if (response == null || response.getData() == null || response.getData().isEmpty()) {
//                            log.error("*****配置{}读取失败,因为路径{}下为空***", config.getName(), path);
//                            lost++;
//                            continue;
//                        }
//                        Map<String, Object> data = response.getData();
//                        Object innerData = data.get("data");
//                        if (!(innerData instanceof Map)) {
//                            log.error("*****配置{}读取失败,路径{}响应结构异常***", config.getName(), path);
//                            lost++;
//                            continue;
//                        }
//                        @SuppressWarnings("unchecked")
//                        Map<String, Object> kvData = (Map<String, Object>) innerData;
//                        if (kvData.isEmpty()) {
//                            log.error("*****配置{}读取失败,因为路径{}下数据为空***", config.getName(), path);
//                            lost++;
//                            continue;
//                        }
//                        String name = kvData.keySet().iterator().next();
//                        if (kvData.size() != 1)
//                            log.warn("*****配置{}读取在路径{}下发现多个密钥,随机选取一个{},\n全部密钥名称{}\n***********"
//                                    , config.getName(), path, name, kvData.keySet());
//                        apiKey = kvData.get(name).toString();
//                        if (MyStringUtil.isNull(apiKey)) {
//                            log.error("*****配置{}读取失败,因为路径{}下读取的密钥{}为空***", config.getName(), path, name);
//                            lost++;
//                            continue;
//                        }
//                    } catch (Exception e) {
//                        log.info("*****配置{}读取失败,失败路径{},失败原因{}***", config.getName(), path, e.getMessage());
//                        lost++;
//                        continue;
//                    }
//                } else {
//                    apiKey = aesEncryptionUtil.decryptValue(config.getApiKey());
//                }
//                Double temperature = config.getTemperature() != null ? config.getTemperature() / 100.0 : null;
//                Double topP = config.getTopP() != null ? config.getTopP() / 100.0 : null;
//                Double freqPenalty = config.getFrequencyPenalty() != null ? config.getFrequencyPenalty() / 100.0 : null;
//                Double presPenalty = config.getPresencePenalty() != null ? config.getPresencePenalty() / 100.0 : null;
//
//                ChatModel chatModel;
//                AiMerchantType merchantType = AiMerchantType.getByTypeNum(config.getMerchant());
//
//                switch (merchantType) {
//                    case DEEP_SEEK_CLOUD -> {
//                        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
//                                .model(config.getModel())
//                                .temperature(temperature)
//                                .internalToolExecutionEnabled(false)
//                                .topP(topP)
//                                .frequencyPenalty(freqPenalty)
//                                .presencePenalty(presPenalty)
//                                .maxTokens(config.getMaxTokens())
//                                .build();
//
//                        chatModel = DeepSeekChatModel.builder()
//                                .deepSeekApi(DeepSeekApi.builder()
//                                        .apiKey(apiKey)
//                                        .baseUrl(config.getBaseUrl())
//                                        .restClientBuilder(interceptedRestClientBuilder())
//                                        .build())
//                                .defaultOptions(options)
//                                .build();
//                    }
//                    case OLLAMA, OLLAMA_CLOUD -> {
//                        OllamaChatOptions options = OllamaChatOptions.builder()
//                                .model(config.getModel())
//                                .temperature(temperature)
//                                .topP(topP)
//                                .numPredict(config.getMaxTokens())
//                                .build();
//
//                        chatModel = OllamaChatModel.builder()
//                                .ollamaApi(OllamaApi.builder()
//                                        .baseUrl(config.getBaseUrl())
//                                        .restClientBuilder(interceptedRestClientBuilder())
//                                        .build())
//                                .defaultOptions(options)
//                                .build();
//                    }
//                    default -> {
//                        Map json = null;
//                        if (MyStringUtil.isNull(config.getExtraConfigJson()))
//                            json = objectMapper.readValue(config.getExtraConfigJson(), Map.class);
//                        OpenAiChatOptions options = OpenAiChatOptions.builder()
//                                .model(config.getModel())
//                                .temperature(temperature)
//                                .topP(topP)
//                                .frequencyPenalty(freqPenalty)
//                                .presencePenalty(presPenalty)
//                                .maxTokens(config.getMaxTokens())
//                                .extraBody(json)
//                                .build();
//
//                        chatModel = OpenAiChatModel.builder()
//                                .openAiApi(OpenAiApi.builder()
//                                        .apiKey(apiKey)
//                                        .baseUrl(config.getBaseUrl())
//                                        .restClientBuilder(interceptedRestClientBuilder())
//                                        .build())
//                                .defaultOptions(options)
//                                .build();
//                    }
//                }
//                ChatClient.Builder clientBuilder = ChatClient.builder(chatModel);
//                if (MyStringUtil.isNull(config.getContent())) {
//                    clientBuilder.defaultSystem(config.getContent());
//                }
//
//                clientBuilder
//                        .defaultAdvisors()
//                        .defaultAdvisors((List) allAdvisor);
//                map.put(config.getId(), clientBuilder.build());
//                ok++;
//                log.info("成功加载AI模型: id={}, name={}, merchant={}", config.getId(), config.getName(), merchantType.getName());
//            } catch (Exception e) {
//                log.error("***********************AI配置文件[{}]加载失败: {}****************************",
//                        config.getName(), e.getMessage(), e);
//                lost++;
//            }
//        }
//        configMap = Collections.unmodifiableMap(map);
//        log.info("***********************加载成功AI配置项共:{}项, 加载失败:{}项****************************", ok, lost);
//    }
//
//    public void refresh() {
//        log.info("-----------------------重新加载AI配置项----------------------------");
//        aiInfo();
//        selfInspection();
//        log.info("-----------------------重新加载AI配置项完成----------------------------");
//    }
//
//    public void selfInspection() {
//        AiClientConfigExample example = new AiClientConfigExample();
//        example.createCriteria().andCodeIn(AiConstant.CODE_LIST);
//        List<AiClientConfig> aiClientConfigs = clientConfigMapper.selectByExample(example);
//        if (aiClientConfigs.size() != AiConstant.CODE_LIST.size())
//            log.warn("警告:实际配置的部分和预期不一致,预期配置数量{},实际为:{}", AiConstant.CODE_LIST.size(), aiClientConfigs.size());
//        HashMap<String, String> codes = new HashMap<>(AiConstant.CODE_LIST.size());
//        for (AiClientConfig config : aiClientConfigs) {
//            ChatClient chatClient = getChatClient(config.getId());
//            if (chatClient == null)
//                log.warn("警告:配置{}初始化失败!", config.getCode());
//            codes.put(config.getCode(), chatClient == null ? "失败" : "成功");
//        }
//        log.info("初始化完成检查,检查结果如下:");
//        if (codes.size() == AiConstant.CODE_LIST.size()) {
//            codes.forEach((k, v) -> log.info("默认配置代码:{},初始化结果:{}", k, v));
//        } else {
//            for (String s : AiConstant.CODE_LIST) {
//                String r = codes.get(s);
//                if (MyStringUtil.isNull(r))
//                    log.info("配置{}尚未配置", s);
//                else
//                    log.info("默认配置代码:{},初始化结果:{}", s, r);
//            }
//        }
//    }
//}