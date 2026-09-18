package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.myUtil.IdUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaMessageNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaSessionNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.repository.MsgRepository;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import com.huaji.galgamebyhuaji.vignaAiFrame.vo.VignaMsgTree;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.NamedPath;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Statement;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

import static org.neo4j.cypherdsl.core.Cypher.node;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiChatMsgServiceImpl implements AiChatMsgService {
    private final Neo4jTemplate neo4jTemplate;
    private final MsgRepository msgRepository;
    
    @Override
    public boolean hasSession(String sessionId) {
        return neo4jTemplate.findById(sessionId, VignaSessionNode.class).isPresent();
    }
    
    @Override
    public List<VignaMsg> getMsgList(String sessionId, String msgId, int size) {
        if (MyStringUtil.isNull(sessionId) || size <= 0) return List.of();
        List<VignaMessageNode> nodeList = queryMessageChain(sessionId, msgId);
        if (nodeList.isEmpty()) return List.of();
        //理论上 Neo4j 返回结果的顺序不能作为业务依据,因此统一按照 turnIndex 从新到旧排序。
        nodeList.sort(Comparator.comparingInt(VignaMessageNode::getTurnIndex).reversed());
        List<VignaMsg> result = new ArrayList<>();
        int summaryCount = 0;
        for (VignaMessageNode node : nodeList) {
            // 错误消息不进入 AI 有效上下文
            if (Boolean.TRUE.equals(node.getError())) continue;
            // 达到指定总结段数量后停止。
            if (summaryCount >= size) break;
            if (Boolean.TRUE.equals(node.getIsSummary())) {
                summaryCount++;
                continue;//不把总结信息塞进去,因为总结只标记了用户发送的请求
                //总结节点的模式: 用户身份调用总结提示词发送的请求->ai回复
            }
            VignaMsg msg = new VignaMsg(node);
            result.add(msg);
        }
        // 当前遍历为新 -> 旧，最终返回旧 -> 新
        Collections.reverse(result);
        return result;
    }
    
    private List<VignaMessageNode> queryMessageChain(String sessionId, String msgId) {
        Node msg = node("VignaMessage").named("msg");
        Node previous = node("VignaMessage").named("p");
        NamedPath path = Cypher.path("path").definedBy(
                msg.relationshipTo(previous, "LAST").min(0));
        Statement statement;
        if (MyStringUtil.isNull(msgId)) {
            Node session = node("VignaSession").named("s");
            statement = Cypher.match(session)
                    .where(session.property("sessionId")
                                   .isEqualTo(Cypher.parameter("sessionId", sessionId)))
                    .match(msg)
                    .where(msg.property("messageId")
                                   .isEqualTo(session.property("tailId")))
                    .match(path)
                    .where(Cypher.not(
                            previous.relationshipTo(Cypher.anyNode(), "LAST")
                    ))
                    .returning(Cypher.name("path"))
                    .build();
        } else {
            statement = Cypher.match(msg)
                    .where(msg.property("messageId")
                                   .isEqualTo(Cypher.parameter("messageId", msgId)))
                    .match(path)
                    .where(Cypher.not(
                            previous.relationshipTo(Cypher.anyNode(), "LAST")
                    ))
                    .returning(Cypher.name("path"))
                    .build();
        }
        Collection<VignaMessageNode> nodes = msgRepository.findAll(statement, VignaMessageNode.class);
        if (nodes.isEmpty()) return List.of();
        return new ArrayList<>(nodes);
    }
    
    @Override
    public List<VignaMsg> getMsgList(String session, int size) {
        return getMsgList(session, null, size);
    }
    
    @Override
    @Transactional(transactionManager = "neo4jTransactionManager")
    public VignaMessageNode editData(String msgId, String sessionId, VignaMsg msg, Long clientID) {
        //检查是否满足插入条件
        if (msg == null || MyStringUtil.isNull(msgId))
            throw new OperationException("修改消息失败!因为需要保存的信息为空!");
        // 查找被编辑的旧节点
        VignaMessageNode oldNode = neo4jTemplate.findById(msgId, VignaMessageNode.class)
                .orElseThrow(() -> new OperationException("消息修改失败!因为修改的目标对话不存在"));
        neo4jTemplate.findById(sessionId, VignaSessionNode.class)
                .orElseThrow(() -> new OperationException("消息保存失败! 会话不存在"));
        VignaMessageNode newNode = new VignaMessageNode();
        newNode.setRole(msg.getRole().getCode());
        newNode.setContent(msg.getContent());
        newNode.setTurnIndex(msg.getIndex());
        newNode.setClientId(clientID);
        // 新节点的前驱是 oldNode 的前驱（即跳过 oldNode）
        newNode.setLastMessage(oldNode.getLastMessage());
        newNode.setIsSummary(msg.getRole() == VignaRole.sum);
        newNode.setTimestamp(OffsetDateTime.now());
        // 保存新节点（更新会话 tailId）
        VignaMessageNode saved = setData(newNode, sessionId, true);
        oldNode.getModifiedVersions().add(saved);
        neo4jTemplate.save(oldNode);
        return saved;
        
    }
    
    @Override
    @Transactional(transactionManager = "neo4jTransactionManager")
    public VignaMessageNode retry(String msgId, String sessionId, VignaMsg msg, Long clientID) {
        //检查是否满足插入条件
        if (msg == null || MyStringUtil.isNull(msgId))
            throw new OperationException("修改消息失败!因为需要保存的信息为空!");
        //检查是否存在节点
        VignaMessageNode oldNode = neo4jTemplate.findById(msgId, VignaMessageNode.class)
                .orElseThrow(() -> new OperationException("消息修改失败!因为修改的目标对话不存在"));
        neo4jTemplate.findById(sessionId, VignaSessionNode.class)
                .orElseThrow(() -> new OperationException("消息保存失败! 会话不存在"));
        // 构造新节点
        VignaMessageNode newNode = new VignaMessageNode();
        newNode.setRole(msg.getRole().getCode());
        newNode.setContent(msg.getContent());
        newNode.setTurnIndex(msg.getIndex());
        newNode.setClientId(clientID);
        // 新节点的前驱是 oldNode 的前驱（即跳过 oldNode）
        newNode.setLastMessage(oldNode.getLastMessage());
        newNode.setIsSummary(msg.getRole() == VignaRole.sum);
        newNode.setTimestamp(OffsetDateTime.now());
        VignaMessageNode saved = setData(newNode, sessionId, true);
        oldNode.getRetriedVersions().add(saved);
        neo4jTemplate.save(oldNode);
        // 保存新节点（更新会话 tailId）
        return saved;
    }
    
    @Override
    @Transactional(transactionManager = "neo4jTransactionManager")
    public VignaMessageNode setData(VignaMessageNode msg, String sessionId, boolean updateSession) {
        if (msg == null) throw new OperationException("消息内容为空");
        VignaSessionNode session = neo4jTemplate.findById(sessionId, VignaSessionNode.class)
                .orElseThrow(() -> new OperationException("消息保存失败! 会话不存在"));
        //处理前置节点
        VignaMessageNode lastNode = msg.getLastMessage();
        if (lastNode != null) {
            // 验证前置节点确实存在于数据库中（防止脏数据）
            if (neo4jTemplate.findById(lastNode.getMessageId(), VignaMessageNode.class).isEmpty())
                throw new OperationException("消息保存失败! 前置消息不存在");
        }
        //生成新 ID 并填充消息对象
        String id = IdUtil.getRandomId("msg");
        msg.setMessageId(id);
        msg.setSessionId(sessionId);
        if (updateSession) {
            session.setTailId(id);
            neo4jTemplate.save(session);
        }
        return neo4jTemplate.save(msg);
    }
    
    @Override
    public VignaMessageNode getMsgNode(String msgId) {
        Optional<VignaMessageNode> byId = neo4jTemplate.findById(msgId, VignaMessageNode.class);
        return byId.orElse(null);
    }
    
    @Override
    public List<VignaMsg> getMsgByIds(List<String> idList) {
        /*
        MATCH(n:VignaMessage) WHERE n.messageId IN []RETURN n
         */
        Node msg = node("VignaMessage").named("msg");
        Statement statement = Cypher.match(msg)
                .where(msg.property("messageId").in(Cypher.parameter("messageId", idList)))
                .returning("msg").build();
        Collection<VignaMessageNode> all = msgRepository.findAll(statement);
        if (all.isEmpty())
            return List.of();
        return all.stream().map(VignaMsg::new).toList();
    }
    
    @Override
    public List<VignaMsgTree> getTree(String sessionId) {
        /*
            MATCH (n:VignaMessage {sessionId: 'session-002'})
            RETURN {
                id: n.messageId,
                role: n.role,
                content :left( n.content,15),
                timestamp: n.timestamp,
                parentId: head([ (n)-[:LAST]->(p) | p.messageId ]),
                editIds: [ (n)-[:MODIFY]->(m) | m.messageId ],
                sum:n.isSummary,
                retryIds: [ (n)-[:RETRY]->(r) | r.messageId ]
            } AS nodeData
            ORDER BY n.timestamp
         */
        Node n = Cypher.node("VignaMessage").named("n");
        Node p = Cypher.node("VignaMessage").named("p");
        Node m = Cypher.node("VignaMessage").named("m");
        Node r = Cypher.node("VignaMessage").named("r");
        
        var parentIdExpr = Cypher.head(
                Cypher.listBasedOn(n.relationshipTo(p, "LAST"))
                        .returning(p.property("messageId")));
        var editIdsExpr = Cypher.listBasedOn(n.relationshipTo(m, "MODIFY"))
                .returning(m.property("messageId"));
        var retryIdsExpr = Cypher.listBasedOn(n.relationshipTo(r, "RETRY"))
                .returning(r.property("messageId"));
        var contentExpr = Cypher.left(n.property("content"), Cypher.literalOf(15));
        var statement = Cypher.match(n)
                .where(n.property("sessionId")
                               .isEqualTo(Cypher.parameter("sessionId", sessionId)))
                .returning(
                        n.property("messageId").as("id"),
                        n.property("role").as("role"),
                        contentExpr.as("content"),
                        n.property("timestamp").as("timestamp"),
                        parentIdExpr.as("parentId"),
                        editIdsExpr.as("editIds"),
                        n.property("isSummary").as("sum"),
                        retryIdsExpr.as("retryIds"))
                .orderBy(n.property("timestamp"))
                .build();
        Collection<VignaMsgTree> all = neo4jTemplate.findAll(statement, VignaMsgTree.class);
        if (all.isEmpty())
            return List.of();
        return new ArrayList<>(all);
    }
}
