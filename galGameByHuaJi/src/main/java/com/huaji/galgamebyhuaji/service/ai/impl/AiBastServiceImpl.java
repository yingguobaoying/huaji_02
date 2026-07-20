package com.huaji.galgamebyhuaji.service.ai.impl;

import com.huaji.galgamebyhuaji.AOP.ai.AiRecordAdvisor;
import com.huaji.galgamebyhuaji.config.AiClientFactory;
import com.huaji.galgamebyhuaji.constant.AiConstant;
import com.huaji.galgamebyhuaji.constant.AiPromptTemplate;
import com.huaji.galgamebyhuaji.constant.PrefixConstant;
import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
import com.huaji.galgamebyhuaji.dao.AiRecordMapper;
import com.huaji.galgamebyhuaji.entity.AiClientConfig;
import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.AiEnumPackage.AiMerchantType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.AiChatClientParam;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.*;
import com.huaji.galgamebyhuaji.service.ai.AiBastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.vault.core.VaultTemplate;
import reactor.core.publisher.Flux;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@DependsOn({"vaultConfigValidator"})
public class AiBastServiceImpl implements AiBastService {
    private final AiClientConfigMapper aiClientConfigMapper;
    @Value("${spring.cloud.vault.kv.ai-path}")
    private String aiTokenPath;
    @Value("${spring.cloud.vault.kv.backend}")
    private String bastPath;
    @Value("${ai.chat-record-size}")
    private int chatRecordSize;
    private final VaultTemplate vaultTemplate;
    private final AESEncryptionUtil aesEncryptionUtil;
    private final AiClientFactory clientFactory;
    private final AiRecordMapper recordMapper;
    private final TransactionTemplate transactionTemplate;
    
    @Override
    public ReturnResult<AiClientConfigWithBLOBs> getList() {//控制层控制了仅管理员可用,不脱敏了,反正也是加密的
        List<AiClientConfigWithBLOBs> val = aiClientConfigMapper.selectByExampleWithBLOBs(null);
        if (ListUtil.isNull(val)) val = val.stream().peek(v -> v.setApiKey(null)).toList();
        return ReturnResult.isTrue("返回成功", val, null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnResult<AiClientConfigWithBLOBs> add(AiClientConfigWithBLOBs config, String apiKey) {
        if (MyStringUtil.isNull(apiKey))
            throw new OperationException("密钥不可为空");
        config.setId(null);
        if (MyStringUtil.isNull(config.getCode()))
            config.setCode(IdUtil.getRandomId(PrefixConstant.AiClientConfigPrefix));
        else {
            AiClientConfigExample aiClientConfigExample = new AiClientConfigExample();
            aiClientConfigExample.createCriteria().andCodeEqualTo(config.getCode());
            List<AiClientConfig> aiClientConfigs = aiClientConfigMapper.selectByExample(aiClientConfigExample);
            if (!ListUtil.isNull(aiClientConfigs))
                throw new OperationException(
                        "添加失败!因为使用的代码:%s,不唯一,冲突配置:%s".formatted(config.getCode(),
                                                                                  aiClientConfigs.stream().map(AiClientConfig::getCode).toList()));
        }
        if (Boolean.TRUE.equals(config.getKeyIsVault())) {
            WriteError.tryWrite(aiClientConfigMapper.insert(config));
            if (config.getId() == null)//先写入数据库
                throw new OperationException("数据库读写失败");
            //编号规则:模型名称-配置id-YYYY-MM-DD-HH-mm
            String jsonName =
                    String.format("%s-%s-%s",
                                  AiMerchantType.getByTypeNum(config.getMerchant()).getProviderName(),
                                  config.getId(),
                                  new SimpleDateFormat("-yyyy-MM-dd-HH-mm").format(new Date()));
            String path = FileUtil.formatUrl(bastPath, aiTokenPath, jsonName);
            config.setKeyIsVault(true);
            config.setApiKey(jsonName);
            try {
                WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKey(config));
            } catch (Exception e) {
                vaultTemplate.delete(path);
                throw e;
            }
            vaultTemplate.write(path, Map.of("api-Key", apiKey));
        } else {
            config.setApiKey(aesEncryptionUtil.encryptValue(apiKey));
            config.setKeyIsVault(false);
            WriteError.tryWrite(aiClientConfigMapper.insert(config));
        }
        return ReturnResult.isTrue("新建成功", config);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnResult<AiClientConfigWithBLOBs> update(AiClientConfigWithBLOBs aiClientConfig, String apiKey) {
        Boolean keyIsVault = aiClientConfig.getKeyIsVault();
        AiClientConfigWithBLOBs old = aiClientConfigMapper.selectByPrimaryKey(aiClientConfig.getId());
        aiClientConfig.setCode(null);//禁止修改引用代码
        if (old == null || old.getId() == null)
            throw new OperationException("错误不存在的数据");
        if (Boolean.TRUE.equals(keyIsVault)) {
            if (MyStringUtil.isNull(apiKey))
                throw new OperationException("修改密钥托管时必须重新修改密钥!");
            if (old.getKeyIsVault()) {//旧的在
                String path = FileUtil.formatUrl(bastPath, aiTokenPath, old.getApiKey());
                WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
                vaultTemplate.write(path, Map.of("api-Key", apiKey));
            } else {//旧的不在
                String jsonName = String.format("%s-%s-%s",
                                                AiMerchantType.getByTypeNum(aiClientConfig.getMerchant()).getProviderName(),
                                                aiClientConfig.getId(),
                                                new SimpleDateFormat("-yyyy-MM-dd-HH-mm").format(new Date()));
                String path = FileUtil.formatUrl(bastPath, aiTokenPath, jsonName);
                aiClientConfig.setApiKey(jsonName);
                WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
                vaultTemplate.write(path, Map.of("api-Key", apiKey));
            }
        } else {//新的不在vault里面或者没有修改
            boolean hasKey = !MyStringUtil.isNull(apiKey);
            if (hasKey) {aiClientConfig.setApiKey(aesEncryptionUtil.encryptValue(apiKey));} else {
                aiClientConfig.setKeyIsVault(old.getKeyIsVault());
                aiClientConfig.setApiKey(old.getApiKey());//防止前端乱来
            }
            try {
                WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
            } catch (Exception e) {
                log.error("更新数据库时出错", e);
                throw new OperationException("密钥更新成功,但是其余部分更新失败");
            }
            if (old.getKeyIsVault() && hasKey) {//旧的在并且更新密钥
                String path = FileUtil.formatUrl(bastPath, aiTokenPath, old.getApiKey());
                vaultTemplate.delete(path);
            }
        }
        return ReturnResult.isTrue("修改完成", aiClientConfig);
    }
    
    @Override
    @Transactional
    public ReturnResult<Void> updateState(Long id, boolean newState) {
        AiClientConfigWithBLOBs aiClientConfigWithBLOBs = new AiClientConfigWithBLOBs();
        aiClientConfigWithBLOBs.setId(id);
        aiClientConfigWithBLOBs.setIsActive(newState);
        WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfigWithBLOBs));
        return ReturnResult.isTrue("状态更新成功", null);
    }
    
    @Override
    public ReturnResult<Map<String, Integer>> getAiMerchantType() {
        Map<String, Integer> map = Arrays.stream(
                AiMerchantType.values()).collect(Collectors.toMap(
                AiMerchantType::getName, AiMerchantType::getCode));
        return ReturnResult.isTrue("获取成功", map);
    }
    
    @Override
    public ReturnResult<String> sumUpRecorder(List<AiRecordWithBLOBs> messageList) {
        if (ListUtil.isNull(messageList) || messageList.stream().noneMatch(Objects::nonNull)) {
            return ReturnResult.isFalse("没有需要总结的记录");
        }
        int maxIndex = 1;
        AiChatClientParam aiChatClientParam = new AiChatClientParam();
        Long userId = messageList.getFirst().getUserId();
        aiChatClientParam.setUserId(userId);
        String sessionId = messageList.getFirst().getSessionId();
        aiChatClientParam.setSessionId(sessionId);
        aiChatClientParam.setPromptContent(AiPromptTemplate.CHAT_SUMMARY_PROMPT);
        StringBuilder sb = new StringBuilder(4096); //预估容量，减少扩容
        sb.append("【聊天记录开始】\n");
        List<Integer> index = new ArrayList<>(messageList.size());
        for (int i = 0; i < messageList.size(); i++) {
            AiRecordWithBLOBs m = messageList.get(i);
            if (m == null || MyStringUtil.isNull(m.getContent())) continue;
            //内容清洗 + 分隔符,此处进行清洗是为了将用户输入与系统拼接的东西隔离开
            String content = m.getContent()
                    .replace("【", "[")
                    .replace("】", "]")
                    .replaceAll("\\n{3,}", "\n\n"); // 压缩连续换行
            sb.append("### 记录").append(i).append(" ###\n")
                    .append(content)
                    .append("\n【时间】: ")
                    .append(TimeUtil.getVisualDateFormatTime(m.getCreatedAt()))
                    .append("\n\n");
            index.add(m.getIndex());
            if (maxIndex < m.getIndex())
                maxIndex = m.getIndex();
        }
        aiChatClientParam.setIndex(maxIndex + 1);
        sb.append("【聊天记录结束】\n【当前时间】: ")
                .append(TimeUtil.getVisualDateFormatTime());
        aiChatClientParam.setUserContent(sb.toString());
        aiChatClientParam.setMessageList(List.of());
        aiChatClientParam.setSumUp(true);
        // 返回前轻量校验（兜底防模型格式偏移）
        log.info("开始总结聊天记录,聊天记录归属用户:{},sessionId:{},总结记录索引:{}",
                 userId, sessionId, index
        );
        ReturnResult<String> result = aiChat(null, AiConstant.SUM_UP_CODE, aiChatClientParam);
        if (result.isOperationResult() && !MyStringUtil.isNull(result.getReturnResult())) {
            String summary = result.getReturnResult().trim();
            if (!summary.contains("# 聊天信息总结") || !summary.contains("# 待办事项总结"))
                log.warn("总结格式异常: {}", summary);
        } else {
            log.error("错误!聊天信息总结失败");
        }
        return result;
    }
    
    
    @Override
    public Flux<String> aiChatByStream(Long clientId, String code, AiChatClientParam param) {
        return aiChatByStream(clientId, code, param, null);
    }
    
    @Override
    public Flux<String> aiChatByStream(Long clientId, String code, AiChatClientParam param, AiClientConfigWithBLOBs config) {
        Long useClientId = getClientId(clientId, code, param);
        // 1. 通过工厂获取已有的 ChatClient 实例
        ChatClient chatClient = clientFactory.getChatClient(useClientId);
        if (chatClient == null) {
            return Flux.error(new OperationException("ai客户端调用失败"));
        }
        // 获取历史记录
        List<AiRecordWithBLOBs> latest = recordMapper.getLatestBySize(chatRecordSize, param.getUserId(), param.getSessionId());
        param.setMessageList(latest);
        if (ListUtil.isNull(latest))
            param.setIndex(1);
        boolean hasTempConfig = config != null;
        if (hasTempConfig) {
            param.setPromptContent(config.getContent());
        }
        // 使用 chatClient 的 Fluent API 构建请求
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .user(param.getUserContent())
                .advisors(advisor -> advisor.param(AiChatClientParam.PARAM_KEY, param));
        // 设置系统提示词
        if (MyStringUtil.isNull(param.getPromptContent())) {
            if (hasTempConfig && !MyStringUtil.isNull(config.getContent())) {
                requestSpec.system(config.getContent());
            }
        } else {
            requestSpec.system(param.getPromptContent());
        }
        // 设置模型参数 (通过 options 方法)
        if (hasTempConfig) {
            ChatOptions.Builder optionsBuilder = ChatOptions.builder();
            if (config.getMaxTokens() != null) {
                optionsBuilder.maxTokens(config.getMaxTokens());
            }
            if (config.getTemperature() != null) {
                optionsBuilder.temperature(config.getTemperature() / 100.0);
            }
            if (config.getTopP() != null) {
                optionsBuilder.topP(config.getTopP() / 100.0);
            }
            if (config.getFrequencyPenalty() != null) {
                optionsBuilder.frequencyPenalty(config.getFrequencyPenalty() / 100.0);
            }
            if (config.getPresencePenalty() != null) {
                optionsBuilder.presencePenalty(config.getPresencePenalty() / 100.0);
            }
            requestSpec.options(optionsBuilder.build());
        }
        StringBuilder fullResponseBuilder = new StringBuilder();
        // 执行流式调用并返回 Flux<String> 内容流
        return requestSpec.stream()
                .content()
                .doOnNext(fullResponseBuilder::append)
                .doOnComplete(() -> {
                    String aiContent = fullResponseBuilder.toString();
                    if (MyStringUtil.isNull(aiContent)) aiContent = "AI响应为空";
                    AiRecordAdvisor.writeRecord(param, aiContent, transactionTemplate, recordMapper);
                });
    }
    
    @Override
    public ReturnResult<String> aiChat(Long clientId, String code, AiChatClientParam param, AiClientConfigWithBLOBs config) {
        Long useClientId = getClientId(clientId, code, param);
        // 1. 通过工厂获取已有的 ChatClient 实例
        ChatClient chatClient = clientFactory.getChatClient(useClientId);
        if (chatClient == null) {
            throw new OperationException("ai客户端调用失败");
        }
        // 获取历史记录
        List<AiRecordWithBLOBs> latest = recordMapper.getLatestBySize(chatRecordSize, param.getUserId(), param.getSessionId());
        param.setMessageList(latest);
        if(ListUtil.isNull(latest))
            param.setIndex(1);
        boolean hasTempConfig = config != null;
        if (hasTempConfig) {
            param.setPromptContent(config.getContent());
        }
        //使用 chatClient 的 Fluent API 构建请求
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .user(param.getUserContent()) // 设置用户消息内容
                .advisors(advisor -> advisor.param(AiChatClientParam.PARAM_KEY, param)); // 配置顾问
        //设置系统提示词
        if (MyStringUtil.isNull(param.getPromptContent())) {
            if (hasTempConfig && !MyStringUtil.isNull(config.getContent())) {
                requestSpec.system(config.getContent());
            }
        } else {
            requestSpec.system(param.getPromptContent());
        }
        //设置模型参数 (通过 options 方法)
        if (hasTempConfig) {
            ChatOptions.Builder optionsBuilder = ChatOptions.builder();
            // 设置各种参数
            if (config.getMaxTokens() != null) {
                optionsBuilder.maxTokens(config.getMaxTokens());
            }
            if (config.getTemperature() != null) {
                optionsBuilder.temperature(config.getTemperature() / 100.0);
            }
            if (config.getTopP() != null) {
                optionsBuilder.topP(config.getTopP() / 100.0);
            }
            if (config.getFrequencyPenalty() != null) {
                optionsBuilder.frequencyPenalty(config.getFrequencyPenalty() / 100.0);
            }
            if (config.getPresencePenalty() != null) {
                optionsBuilder.presencePenalty(config.getPresencePenalty() / 100.0);
            }
            requestSpec.options(optionsBuilder.build());
        }
        //执行调用并获取响应
        String responseContent = requestSpec.call()
                .content(); // 直接获取响应文本
        return ReturnResult.isTrue("响应完成", responseContent);
    }
    
    
    private Long getClientId(Long clientId, String code, AiChatClientParam param) {
        Long finalId = null;
        if (clientId == null) {
            if (!MyStringUtil.isNull(code)) {
                AiClientConfigExample example = new AiClientConfigExample();
                example.createCriteria().andCodeEqualTo(code);
                List<AiClientConfig> aiClientConfigs = aiClientConfigMapper.selectByExample(example);
                if (!ListUtil.isNull(aiClientConfigs))
                    finalId = aiClientConfigs.getFirst().getId();
            }
            if (finalId == null && param != null)
                finalId = param.getClientId();
        } else {
            finalId = clientId;
        }
        if (finalId == null)
            throw new OperationException("未检测到调用目标");
        return finalId;
    }
    
    @Override
    public ReturnResult<String> aiChat(Long clientId, String code, AiChatClientParam param) {
        return aiChat(clientId, code, param, null);
    }
    
}
