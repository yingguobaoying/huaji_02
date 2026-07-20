package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.AiEnumPackage.MsgType;
import com.huaji.galgamebyhuaji.model.AiChatClientParam;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.service.ai.AiBastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取聊天记录的切面
 */
@Component
@Slf4j
public class DatabaseChatMemory extends MyBaseAdvisor {
    @Value("${ai.chat-record-size}")
    private int chatRecordSize;
    
    private final AiBastService chatService;
    
    public final static String REQUEST_JSON = "request_json";
    
    public DatabaseChatMemory(AiBastService chatService) {
        this.chatService = chatService;
    }
    
    /**
     * 构建历史消息列表，合并到当前 Prompt 中
     */
    private ChatClientRequest buildRequestWithHistory(ChatClientRequest chatClientRequest, AiChatClientParam param) {
        // 参数为空，直接返回原请求
        if (param == null) {
            saveData(chatClientRequest, REQUEST_JSON, chatClientRequest);
            return chatClientRequest;
        }
        // 获取历史记录
        List<AiRecordWithBLOBs> lastRecord = param.getMessageList();
        // 无历史记录，直接放行
        if (lastRecord == null || lastRecord.isEmpty()) {
            saveData(chatClientRequest, REQUEST_JSON, chatClientRequest);
            return chatClientRequest;
        }
        // 构建历史消息列表
        List<Message> messageList = new ArrayList<>(lastRecord.size() + 2);
        boolean needSumUp = lastRecord.size() >= chatRecordSize; // 达到保留轮数时触发总结
        for (AiRecordWithBLOBs record : lastRecord) {
            if (record.getRole() == null) {
                messageList.add(new UserMessage(record.getContent()));
                continue;
            }
            switch (MsgType.getType(record.getRole())) {
                case USER -> messageList.add(new UserMessage(record.getContent()));
                case SYSTEM -> log.debug("忽略历史系统消息: {}", record.getContent());
                case AI_MSG -> messageList.add(new AssistantMessage(record.getContent()));
                case SUMMARY -> {
                    needSumUp = false;
                    messageList.add(new SystemMessage("以下为之前用户的上下文总结: " + record.getContent()));
                }
                default -> log.warn("未知消息角色: {}, 内容: {}", record.getRole(), record.getContent());
            }
        }
        
        // 如果需要自动总结
        if (needSumUp) {
            try {
                ReturnResult<String> summaryResult = chatService.sumUpRecorder(lastRecord);
                if (summaryResult.isOperationResult()) {
                    messageList.add(new SystemMessage(summaryResult.getReturnResult()));
                }
            } catch (Exception e) {
                log.error("自动总结上下文失败", e);
            }
        }
        
        Prompt currentPrompt = chatClientRequest.prompt();
        List<SystemMessage> currentMessages = currentPrompt.getSystemMessages();
        Message currentSystemMsg = currentMessages.stream()
                .filter(m -> m.getMessageType() == MessageType.SYSTEM)
                .findFirst()
                .orElse(null);
        Message currentUserMsg = currentMessages.stream()
                .filter(m -> m.getMessageType() == MessageType.USER)
                .reduce((first, second) -> second)
                .orElse(null);
        if (currentUserMsg == null) {
            log.warn("当前请求中缺少 USER 消息，可能无法正常响应");
        }
        
        List<Message> finalMessages = new ArrayList<>();
        if (currentSystemMsg != null) finalMessages.add(currentSystemMsg);
        finalMessages.addAll(messageList);
        if (currentUserMsg != null) finalMessages.add(currentUserMsg);
        
        Prompt newPrompt = new Prompt(finalMessages, currentPrompt.getOptions());
        ChatClientRequest newRequest = chatClientRequest.mutate()
                .prompt(newPrompt)
                .build();
        saveData(chatClientRequest, REQUEST_JSON, newRequest);
        return newRequest;
    }
    
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        AiChatClientParam param = (AiChatClientParam) getParam(chatClientRequest, AiChatClientParam.PARAM_KEY);
        ChatClientRequest newRequest = buildRequestWithHistory(chatClientRequest, param);
        return callAdvisorChain.nextCall(newRequest);
    }
    
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        AiChatClientParam param = (AiChatClientParam) getParam(chatClientRequest, AiChatClientParam.PARAM_KEY);
        ChatClientRequest newRequest = buildRequestWithHistory(chatClientRequest, param);
        return streamAdvisorChain.nextStream(newRequest);
    }
    
    
    @Override
    public int getOrder() {
        return 0;
    }
}
