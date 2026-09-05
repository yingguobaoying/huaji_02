package com.huaji.galgamebyhuaji.vignaAiFrame.node;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.OffsetDateTime;

/**
 * 会话节点 (Session)
 * 作为对话的容器与全局游标，维护当前对话流的末端。
 */
@Data
@NoArgsConstructor
@Node("VignaSession")
public class VignaSessionNode {
    /**
     * 会话唯一标识 (主键)
     */
    @Id
    private String sessionId;
    /**
     * 所属用户 ID
     */
    private Integer userId;
    /**
     * 核心游标：当前生效对话流的末端节点 ID
     */
    private String tailId;
    /**
     * 会话创建时间
     */
    private OffsetDateTime createdAt;
    
}
