package com.huaji.galgamebyhuaji.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

@Getter
@Setter
public class AiChatClientParam {
    private Long userId;
    private Long clientId;
    private String sessionId;
    private String promptContent;
    private String userContent;
    private List<Message> messageList;
    /**
     * 对话索引,记录当前对话为第几条
     * 比如
     * 用户 - 你好,简单介绍一下自己 index :1
     * ai - deekseep.... :2
     * 用户 - xxx :3
     * ...
     */
    private int index;
    public final static String PARAM_KEY = "Vigna_loveliness";
}
