package com.huaji.galgamebyhuaji.model;

import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiChatClientParam {
    private Integer userId;
    private Long clientId;
    private String sessionId;
    private String promptContent;
    private String userContent;
    private List<AiRecordWithBLOBs> messageList;
    private boolean isSumUp=false;
    /**
     * 对话索引,记录当前对话为第几条
     * 比如
     * 用户 - 你好,简单介绍一下自己 index :1
     * ai - deekseep.... :2
     * 用户 - xxx :3
     * ...
     */
    private int index;
    /** HTTP 拦截器分配的唯一记录 ID，流式场景用于在 doOnComplete 中消费 */
    private long httpRecordId;
    public final static String PARAM_KEY = "Vigna_loveliness";
}
