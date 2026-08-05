package com.huaji.galgamebyhuaji.dto;

import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import lombok.Data;

@Data
public class ChatParam {
    /**
     * 会话id
     */
    String sessionId;
    /**
     * 输入
     */
    @CustomNotNull
    String content;
    @CustomNotNull
    Long  clientId;
    
}
