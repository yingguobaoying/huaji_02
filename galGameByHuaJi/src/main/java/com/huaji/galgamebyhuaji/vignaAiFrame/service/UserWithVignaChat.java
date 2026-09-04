package com.huaji.galgamebyhuaji.vignaAiFrame.service;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;

import java.util.List;
import java.util.Map;

public interface UserWithVignaChat {
    /**
     * 获取用户有权限的配置(id + 名称+描述)
     */
    List<AiClientConfigWithBLOBs> getList(int userId);
    
    /**
     * 获取用户的聊天会话id和首条消息的预览
     */
    Map<String, VignaMsg> getUserSession(int userId);
}
