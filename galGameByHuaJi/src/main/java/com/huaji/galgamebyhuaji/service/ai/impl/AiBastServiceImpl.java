package com.huaji.galgamebyhuaji.service.ai.impl;

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
import com.huaji.galgamebyhuaji.enumPackage.AiEnumPackage.MsgType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.AiChatClientParam;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.AESEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.IdUtil;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.ai.AiBastService;
import com.huaji.galgamebyhuaji.service.ai.AiChatMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.core.VaultTemplate;
import reactor.core.publisher.Flux;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@DependsOn({ "vaultConfigValidator" })
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
    private final AiRecordMapper recordMapper;
    private final AiChatMsgService aiChatMsgService;

    private String dataPath(String sub) {
        return bastPath + "/data/" + aiTokenPath + "/" + sub;
    }

    private String metadataPath(String sub) {
        return bastPath + "/metadata/" + aiTokenPath + "/" + sub;
    }

    private Map<String, Object> wrapKv2Body(Map<String, Object> kvData) {
        return Map.of("data", kvData);
    }

    @Override
    public ReturnResult<AiClientConfigWithBLOBs> getList() {
        List<AiClientConfigWithBLOBs> val = aiClientConfigMapper.selectByExampleWithBLOBs(null);
        if (!ListUtil.isNull(val))
            val = val.stream().peek(v -> v.setApiKey(null)).toList();
        return ReturnResult.isTrue("返回成功", val, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnResult<AiClientConfigWithBLOBs> add(AiClientConfigWithBLOBs config, String apiKey) {
        if (MyStringUtil.isNull(apiKey))
            throw new OperationException("密钥不可为空");
        config.setId(null);
        config.setApiKey(AiClientFactory.API_KEY_PLACEHOLDER);
        config.setCreatedAt(new Date());
        config.setUpdatedAt(new Date());
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
            if (config.getId() == null)
                throw new OperationException("数据库读写失败");
            String jsonName = String.format("%s-%s-%s",
                    AiMerchantType.getByTypeNum(config.getMerchant()).getProviderName(),
                    config.getId(),
                    new SimpleDateFormat("-yyyy-MM-dd-HH-mm").format(new Date()));
            String path = dataPath(jsonName);
            config.setKeyIsVault(true);
            config.setApiKey(jsonName);
            try {
                WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeyWithBLOBs(config));
            } catch (Exception e) {
                vaultTemplate.delete(metadataPath(jsonName));
                throw e;
            }
            vaultTemplate.write(path, wrapKv2Body(Map.of("api-Key", apiKey)));
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
        aiClientConfig.setCode(null);
        if (old == null || old.getId() == null)
            throw new OperationException("错误不存在的数据");
        if (Boolean.TRUE.equals(keyIsVault)) {
            if (MyStringUtil.isNull(apiKey))
                throw new OperationException("修改密钥托管时必须重新修改密钥!");
            if (old.getKeyIsVault()) {
                String path = dataPath(old.getApiKey());
                WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
                vaultTemplate.write(path, wrapKv2Body(Map.of("api-Key", apiKey)));
            } else {
                String jsonName = String.format("%s-%s-%s",
                        AiMerchantType.getByTypeNum(aiClientConfig.getMerchant()).getProviderName(),
                        aiClientConfig.getId(),
                        new SimpleDateFormat("-yyyy-MM-dd-HH-mm").format(new Date()));
                String path = dataPath(jsonName);
                aiClientConfig.setApiKey(jsonName);
                WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
                vaultTemplate.write(path, wrapKv2Body(Map.of("api-Key", apiKey)));
            }
        } else {
            boolean hasKey = !MyStringUtil.isNull(apiKey);
            if (hasKey) {
                aiClientConfig.setApiKey(aesEncryptionUtil.encryptValue(apiKey));
            } else {
                aiClientConfig.setKeyIsVault(old.getKeyIsVault());
                aiClientConfig.setApiKey(old.getApiKey());
            }
            try {
                WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
            } catch (Exception e) {
                log.error("更新数据库时出错", e);
                throw new OperationException("密钥更新成功,但是其余部分更新失败");
            }
            if (old.getKeyIsVault() && hasKey) {
                vaultTemplate.delete(metadataPath(old.getApiKey()));
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
                AiMerchantType.values()).collect(
                        Collectors.toMap(
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
        Integer userId = messageList.getFirst().getUserId();
        aiChatClientParam.setUserId(userId);
        String sessionId = messageList.getFirst().getSessionId();
        aiChatClientParam.setSessionId(sessionId);
        aiChatClientParam.setPromptContent(AiPromptTemplate.CHAT_SUMMARY_PROMPT);
        StringBuilder sb = new StringBuilder(4096);
        sb.append("【聊天记录开始】\n");
        List<Integer> index = new ArrayList<>(messageList.size());
        for (int i = 0; i < messageList.size(); i++) {
            AiRecordWithBLOBs m = messageList.get(i);
            if (m == null || MyStringUtil.isNull(m.getContent()))
                continue;
            String content = m.getContent()
                    .replace("【", "[")
                    .replace("】", "]")
                    .replaceAll("\\n{3,}", "\n\n");
            sb.append("### 记录").append(i).append(" ###\n")
                    .append(content)
                    .append("\n【时间】: ")
                    .append(TimeUtil.getVisualDateFormatTime(m.getCreatedAt()))
                    .append("\n\n");
            index.add(m.getChatIndex());
            if (maxIndex < m.getChatIndex())
                maxIndex = m.getChatIndex();
        }
        aiChatClientParam.setIndex(maxIndex + 1);
        sb.append("【聊天记录结束】\n【当前时间】: ")
                .append(TimeUtil.getVisualDateFormatTime());
        aiChatClientParam.setUserContent(sb.toString());
        aiChatClientParam.setMessageList(List.of());
        aiChatClientParam.setSumUp(true);
        log.info("开始总结聊天记录,聊天记录归属用户:{},sessionId:{},总结记录索引:{}",
                userId, sessionId, index);
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
    public Flux<String> aiChatByStream(Long clientId, String code, AiChatClientParam param,
            AiClientConfigWithBLOBs config) {
        Long useClientId = getClientId(clientId, code, param);
        ChatClient chatClient = AiClientFactory.getChatClient(useClientId);
        if (chatClient == null) {
            return Flux.error(new OperationException("ai客户端调用失败"));
        }
        List<AiRecordWithBLOBs> latest = recordMapper.getLatestBySize(chatRecordSize, param.getUserId(),
                param.getSessionId());
        param.setMessageList(latest);
        if (ListUtil.isNull(latest))
            param.setIndex(1);
        else
            param.setIndex(latest.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(r -> r.getChatIndex() == null ? 0 : r.getChatIndex())
                    .max()
                    .orElse(0) + 1);
        boolean hasTempConfig = config != null;
        if (hasTempConfig) {
            param.setPromptContent(config.getContent());
        }
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .user(param.getUserContent())
                .advisors(advisor -> advisor.param(AiChatClientParam.PARAM_KEY, param));
        if (MyStringUtil.isNull(param.getPromptContent())) {
            if (hasTempConfig && !MyStringUtil.isNull(config.getContent())) {
                requestSpec.system(config.getContent());
            }
        } else {
            requestSpec.system(param.getPromptContent());
        }
        if (hasTempConfig) {
            ChatOptions.Builder optionsBuilder = ChatOptions.builder();
            if (config.getMaxTokens() != null)
                optionsBuilder.maxTokens(config.getMaxTokens());
            if (config.getTemperature() != null)
                optionsBuilder.temperature(config.getTemperature() / 100.0);
            if (config.getTopP() != null)
                optionsBuilder.topP(config.getTopP() / 100.0);
            if (config.getFrequencyPenalty() != null)
                optionsBuilder.frequencyPenalty(config.getFrequencyPenalty() / 100.0);
            if (config.getPresencePenalty() != null)
                optionsBuilder.presencePenalty(config.getPresencePenalty() / 100.0);
            requestSpec.options(optionsBuilder.build());
        }
        StringBuilder fullResponseBuilder = new StringBuilder();
        return requestSpec.stream()
                .content()
                .doOnNext(fullResponseBuilder::append)
                .doOnComplete(() -> {
                    String aiContent = fullResponseBuilder.toString();
                    if (MyStringUtil.isNull(aiContent))
                        aiContent = "AI响应为空";

                    AiRecordWithBLOBs record = new AiRecordWithBLOBs();
                    record.setContent(aiContent);
                    record.setPromptContent(param.getPromptContent());
                    record.setChatIndex(param.getIndex() + 1);
                    record.setUserId(param.getUserId());
                    record.setRole(MsgType.AI_MSG.getType());
                    record.setSessionId(param.getSessionId());
                    aiChatMsgService.installData(record);
                });
    }

    @Override
    public ReturnResult<String> aiChat(Long clientId, String code, AiChatClientParam param,
            AiClientConfigWithBLOBs config) {
        Long useClientId = getClientId(clientId, code, param);
        ChatClient chatClient = AiClientFactory.getChatClient(useClientId);
        if (chatClient == null) {
            throw new OperationException("ai客户端调用失败");
        }
        List<AiRecordWithBLOBs> latest = recordMapper.getLatestBySize(chatRecordSize,
                param.getUserId(), param.getSessionId());
        param.setMessageList(latest);
        if (ListUtil.isNull(latest))
            param.setIndex(1);
        else
            param.setIndex(latest.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(r -> r.getChatIndex() == null ? 0 : r.getChatIndex())
                    .max()
                    .orElse(0) + 1);
        boolean hasTempConfig = config != null;
        if (hasTempConfig) {
            param.setPromptContent(config.getContent());
        }
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .user(param.getUserContent())
                .advisors(advisor -> advisor.param(AiChatClientParam.PARAM_KEY, param));
        if (MyStringUtil.isNull(param.getPromptContent())) {
            if (hasTempConfig && !MyStringUtil.isNull(config.getContent())) {
                requestSpec.system(config.getContent());
            }
        } else {
            requestSpec.system(param.getPromptContent());
        }
        if (hasTempConfig) {
            ChatOptions.Builder optionsBuilder = ChatOptions.builder();
            if (config.getMaxTokens() != null)
                optionsBuilder.maxTokens(config.getMaxTokens());
            if (config.getTemperature() != null)
                optionsBuilder.temperature(config.getTemperature() / 100.0);
            if (config.getTopP() != null)
                optionsBuilder.topP(config.getTopP() / 100.0);
            if (config.getFrequencyPenalty() != null)
                optionsBuilder.frequencyPenalty(config.getFrequencyPenalty() / 100.0);
            if (config.getPresencePenalty() != null)
                optionsBuilder.presencePenalty(config.getPresencePenalty() / 100.0);
            requestSpec.options(optionsBuilder.build());
        }
        // 非流式：adviseCall → saveUserMsg + saveAiMsg 完成全部写入
        String responseContent = requestSpec.call().content();
        if (MyStringUtil.isNull(responseContent))
            responseContent = "AI响应为空";
        log.info("AI响应内容:{}", responseContent);
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