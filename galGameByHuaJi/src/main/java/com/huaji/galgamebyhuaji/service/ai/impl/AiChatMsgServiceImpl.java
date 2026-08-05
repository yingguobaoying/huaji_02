package com.huaji.galgamebyhuaji.service.ai.impl;

import com.huaji.galgamebyhuaji.dao.AiClassificationMapper;
import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
import com.huaji.galgamebyhuaji.dao.AiRecordMapper;
import com.huaji.galgamebyhuaji.entity.AiClassification;
import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
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
    private final AiClassificationMapper classificationMapper;
    
    @Override
    public List<AiRecordWithBLOBs> getFirstRecord(int userId) {
        return recordMapper.getFirstRecord(userId);
    }
    
    @Override
    public List<AiRecordWithBLOBs> getRecord(String sessionId, int userId) {
        return recordMapper.getRecord(userId, sessionId);
    }
    
    @Override
    public List<AiModerList> getAiConfig(int userId) {
        //搜索启用的模型
        List<AiClassification> auth = classificationMapper.getUserViewList(userId);
        if (ListUtil.isNull(auth))
            return List.of();
        AiClientConfigExample example = new AiClientConfigExample();
        example.createCriteria().andIdIn(auth.stream().map(AiClassification::getClientId).toList());
        List<AiClientConfigWithBLOBs> list = clientConfigMapper.selectByExampleWithBLOBs(example);
        return list.stream()
                .map(l -> {
                    AiModerList moder = new AiModerList();
                    moder.setConfigId(l.getId());
                    moder.setCode(l.getCode());
                    moder.setName(l.getName());
                    moder.setContent(l.getContent());
                    return moder;
                }).toList();
    }
}
