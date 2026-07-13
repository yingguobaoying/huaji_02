package com.huaji.galgamebyhuaji.AOP.ai;

import com.huaji.galgamebyhuaji.dao.AiRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
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
        //添加方法在返回回调中进行记录,因为这里能获取的东西太湿
        return;
    }
    
    @Override
    public List<Message> get(String conversationId) {
        //获取最新的20条
        List<Message> list = new ArrayList<>();
        recordMapper.selectByTimeToSize(20,conversationId);
        return List.of();
    }
    
    @Override
    public void clear(String conversationId) {
        //不允许清空
    }
}
