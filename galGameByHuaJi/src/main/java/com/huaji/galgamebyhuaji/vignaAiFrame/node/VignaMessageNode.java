package com.huaji.galgamebyhuaji.vignaAiFrame.node;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Node("VignaMessage")
public class VignaMessageNode {
    @Id
    private String messageId;
    /**
     * 消息角色
     */
    private Integer role;
    /**
     * 消息文本内容
     */
    private String content;
    /**
     * 逻辑轮次索引
     */
    private Integer turnIndex;
    /**
     * 使用的 AI 客户端配置 ID
     */
    private Long clientId;
    /**
     * 创建时间
     */
    private OffsetDateTime timestamp;
    /**
     * 外部系统 ID
     */
    private String externalId;
    /**
     * 输入消耗 Token 数
     */
    private Integer inputTokens;
    /**
     * 输出消耗 Token 数
     */
    private Integer outputTokens;
    /**
     * 发送该消息时使用的完整提示词
     */
    private String promptRaw;
    /**
     * 请求/响应体原始 JSON
     */
    private String json;
    /**
     * 核心标识：是否为压缩节点
     */
    private Boolean isSummary;
    /**
     * 请求是否出错
     */
    private Boolean error;
    private Boolean deleted;
    /**
     * 主干关系：last (上一条)
     * 方向：当前消息 -> 上文消息 (时间倒序)
     */
    @Relationship(type = "LAST", direction = Relationship.Direction.OUTGOING, cascadeUpdates = false)
    private VignaMessageNode lastMessage;
    /**
     * 属于会话
     */
    private String sessionId;
    /**
     * 分支关系：MODIFY (修改)
     * 方向：旧消息 -> 新消息
     */
    @Relationship(type = "MODIFY", direction = Relationship.Direction.OUTGOING, cascadeUpdates = false)
    private List<VignaMessageNode> modifiedVersions;
    
    /**
     * 分支关系：RETRY (重试)
     * 方向：旧消息 -> 新消息
     */
    @Relationship(type = "RETRY", direction = Relationship.Direction.OUTGOING, cascadeUpdates = false)
    private List<VignaMessageNode> retriedVersions;
}
