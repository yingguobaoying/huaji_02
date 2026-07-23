package com.huaji.galgamebyhuaji.service.ai.impl;

import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
import com.huaji.galgamebyhuaji.dao.AiRecordMapper;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.service.ai.AiChatMsgService;
import com.huaji.galgamebyhuaji.vo.AiModerList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiChatMsgServiceImpl implements AiChatMsgService {
    private final AiClientConfigMapper clientConfigMapper;
    private final AiRecordMapper recordMapper;
    
    @Override
    public List<AiRecordWithBLOBs> getFirstRecord(int userId) {
        return recordMapper.getFirstRecord(userId);
    }
    
    @Override
    public List<AiRecordWithBLOBs> getRecord(String sessionId, int userId) {
        return recordMapper.getRecord(userId,sessionId);
    }
    
    @Override
    public List<AiModerList> getAiConfig() {
        List<AiClientConfigWithBLOBs> list= clientConfigMapper.getUserUseModer();
        return List.of();
    }
}
