package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.dao.AiRecordMapper;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.model.AiChatClientParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
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
@RequiredArgsConstructor
public class DatabaseChatMemory extends MyBaseAdvisor {
    private final AiRecordMapper recordMapper;
    @Value("${ai.chat-record-size}")
    private int chatRecordSize;
    
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        AiChatClientParam param = getParam(chatClientRequest);
        //获取历史记录
        List<AiRecordWithBLOBs> lastRecord = recordMapper.getLatestBySize(chatRecordSize, param.getUserId(), param.getSessionId());
        if (lastRecord.isEmpty()) {
            callAdvisorChain.nextCall(chatClientRequest);
            return super.adviseCall(chatClientRequest, callAdvisorChain);
        }
        //有历史记录时记录组成:保留轮数+可能有的1条总结()
        List<Message> messageList = new ArrayList<>(chatRecordSize + 1);
        boolean needSumUp = lastRecord.size() == chatRecordSize;//如果拿到的记录数量少于对话轮数说明不需要总结,等于时才判断
        for (AiRecordWithBLOBs record : lastRecord) {
            //消息角色：1 - 用户，2 - 系统, 3 - ai回复 ,4 - ai总结之前的上下文
            Message m = switch (record.getRole()) {
                case 2 -> {
                    log.warn("警告:对话{}发现额外的系统提示词!{}", record.getId(), record.getContent());
                    yield new SystemMessage(record.getContent());
                }
                case 3 -> new AssistantMessage(record.getContent());
                case 4 -> {
                    needSumUp = false;
                    yield new SystemMessage("以下为之前用户的上下文总结:%s".formatted(record.getContent()));
                }
                case null, default -> new UserMessage(record.getContent());
            };
            messageList.add(m);
        }
        if(needSumUp){//ai总结
        
        }
        return super.adviseCall(chatClientRequest, callAdvisorChain);
    }
    
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return super.adviseStream(chatClientRequest, streamAdvisorChain);
    }
    
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        return null;
    }
    
    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return null;
    }
    
    @Override
    public int getOrder() {
        return 0;
    }
}
