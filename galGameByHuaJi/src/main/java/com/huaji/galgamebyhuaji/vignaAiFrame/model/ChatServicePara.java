package com.huaji.galgamebyhuaji.vignaAiFrame.model;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaMsgType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ChatServicePara {
    /**
     * 用户发送消息
     */
    private VignaMsg msg;
    /**
     * 归属用户
     */
    private int userId;
    /**
     * 使用的配置(优先级高于代码)
     */
    private Long clientId;
    /**
     * 配置代码
     */
    private String code;
    /**
     * 会话ID
     */
    private String sessionId;
    /**
     * 此消息是否为总结信息
     */
    private boolean isSumUp = false;
    /**
     * 覆盖的默认配置
     */
    private AiClientConfigWithBLOBs config;
    /**
     * 额外的json参数
     */
    private Map<String, Object> extraJson;
    /**
     * 回复消息id(如果为空则为会话默认的)
     */
    private String msgId;
    private VignaMsgType type;
}
