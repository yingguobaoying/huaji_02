package com.huaji.galgamebyhuaji.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VignaChatUserPram {
    private String sessionId;
    private String msgId;
    private String content;
    private Long clientId;
    //1: "普通对话" 2: "修改对话" 3: "重试对话"
    private int type = 1;
    /**
     * 获取对话轮数
     */
    private int size = 1;
    
}
