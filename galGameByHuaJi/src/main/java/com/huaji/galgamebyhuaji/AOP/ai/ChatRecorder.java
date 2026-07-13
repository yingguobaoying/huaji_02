package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.dao.AiRecordMapper;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatRecorder implements ChatMemory {
    private final AiRecordMapper recordMapper;
    
    
    @Override
    public void add(String conversationId, List<Message> messages) {
        //添加方法在返回回调中进行记录,因为这里能获取的东西太少
    }
    
    @Override
    public List<Message> get(String conversationId) {
        //格式: userId:sessionId
        String[] split = conversationId.split(":");
        Long userId = Long.parseLong(split[0]);
        String sessionId = split[1];
        //获取最新的20条
        List<Message> list = new ArrayList<>();
        List<AiRecordWithBLOBs> aiRecordWithBLOBs = recordMapper.selectByTimeToSize(20, userId, sessionId);
        if (ListUtil.isNull(aiRecordWithBLOBs))
            return List.of();
        for (AiRecordWithBLOBs bloBs : aiRecordWithBLOBs) {
            //消息角色：1 - 用户，2 - 系统, 3 - ai回复 ,4 - ai总结之前的上下文
            Message m = switch (bloBs.getRole()) {
                case 2 -> {
                    log.warn("警告:对话{}发现额外的系统提示词!{}", bloBs.getId(), bloBs.getContent());
                    yield new SystemMessage(bloBs.getContent());
                }
                case 3 -> new AssistantMessage(bloBs.getContent());
                case 4 -> new SystemMessage("以下为之前用户的上下文总结:%s".formatted(bloBs.getContent()));
                case null, default -> new UserMessage(bloBs.getContent());
            };
            list.add(m);
        }
        return list;
    }
    
    @Override
    public void clear(String conversationId) {
        //不允许清空
    }
}
