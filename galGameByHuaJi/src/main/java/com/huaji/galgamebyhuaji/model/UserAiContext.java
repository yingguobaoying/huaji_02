package com.huaji.galgamebyhuaji.model;

import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import lombok.Data;

import java.util.List;

/**
 * 用户 AI 对话的临时上下文。
 * 在 aiChat/aiChatByStream 入口设置，供 Advisor 和 HTTP Interceptor 跨线程安全读取。
 * 配合 UserAiContextHolder 全局缓存使用。
 */
@Data
public class UserAiContext {
    /** 用户ID */
    private Integer userId;
    /** 会话ID */
    private String sessionId;
    /** 用户输入的内容 */
    private String userContent;
    /** 系统提示词 */
    private String promptContent;
    /** 历史消息列表 */
    private List<AiRecordWithBLOBs> messageList;
    /** 是否为总结消息 */
    private boolean isSumUp;
    /** 当前对话索引 */
    private int index;
    /** HTTP 记录 ID（用于关联 AiHttpRecordInterceptor 的数据） */
    private long httpRecordId;
    /** 上下文创建时间，用于超时清理 */
    private long createdAt;

    public UserAiContext() {
        this.createdAt = System.currentTimeMillis();
    }
}