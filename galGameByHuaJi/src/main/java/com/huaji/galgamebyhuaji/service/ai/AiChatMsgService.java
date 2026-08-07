package com.huaji.galgamebyhuaji.service.ai;

import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.vo.AiModerList;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AiChatMsgService {
    /**
     * 获取用户所有聊天记录中的第一条消息
     */
    List<AiRecordWithBLOBs> getFirstRecord(int userId);
    
    /**
     * 获取用户某个会话全部聊天记录
     */
    List<AiRecordWithBLOBs> getRecord(String sessionId,int userId);
    
    /**
     * 获取所有可用模型列表
     */
    List<AiModerList> getAiConfig(int userId);
    
    @Transactional
    long installData(AiRecordWithBLOBs record);
    
    @Transactional
    void setRecordJson(long id, String json);
}
