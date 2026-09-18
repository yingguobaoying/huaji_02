package com.huaji.galgamebyhuaji.dto;

import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VignaChatUserPram {
    @CustomNotNull(message = "会话信息不可为空")
    private String sessionId;
    private String msgId;
    @CustomNotNull(message = "输入信息不可为空")
    private String content;
    private Long clientId;
    //1: "普通对话" 2: "修改对话" 3: "重试对话"
    private int type = 1;
    /**
     * 获取对话轮数
     */
    private int size = 1;
    
}
