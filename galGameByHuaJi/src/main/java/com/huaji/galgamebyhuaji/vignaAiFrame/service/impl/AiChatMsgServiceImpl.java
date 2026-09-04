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
            if (Boolean.TRUE.equals(node.getIsSummary())) summaryCount++;
            VignaMsg msg = new VignaMsg();
            msg.setRole(VignaRole.getType(node.getRole()));
            msg.setContent(node.getContent());
            msg.setIndex(node.getTurnIndex());
            result.add(msg);
        }
        // 当前遍历为新 -> 旧，最终返回旧 -> 新
        Collections.reverse(result);
        return result;
    }
    
    private List<VignaMessageNode> queryMessageChain(String sessionId, String msgId) {
        Node msg = Cypher.node("VignaMessage").named("msg");
        Node previous = Cypher.node("VignaMessage").named("p");
        NamedPath path = Cypher.path("path").definedBy(
                msg.relationshipTo(previous, "LAST").min(0));
        Statement statement;
        if (MyStringUtil.isNull(msgId)) {
            Node session = Cypher.node("VignaSession").named("s");
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
    @Transactional
    public VignaMessageNode editData(String msgId, String sessionId, VignaMsg msg, Long clientID) {
        //检查是否满足插入条件
        if (msg == null || MyStringUtil.isNull(msgId))
            throw new OperationException("修改消息失败!因为必要信息为空!");
        //检查是否存在节点
        Optional<VignaMessageNode> lastMsg = neo4jTemplate.findById(msgId, VignaMessageNode.class);
        if (lastMsg.isEmpty())
            throw new OperationException("修改消息失败!因为前置信息不存在!");
        VignaMessageNode oldNode = lastMsg.get();
        VignaMessageNode node = new VignaMessageNode();
        node.setRole(msg.getRole().getCode());
        node.setContent(msg.getContent());
        node.setTurnIndex(msg.getIndex());
        node.setClientId(clientID);
        node.setLastMessage(oldNode.getLastMessage());
        node.setIsSummary(msg.getRole() == VignaRole.sum);
        node.setTimestamp(OffsetDateTime.now());
        VignaMessageNode node1 = setData(node, sessionId, true);
        oldNode.getModifiedVersions().add(node);
        neo4jTemplate.save(oldNode);
        return node1;
        
    }
    
    @Override
    @Transactional
    public VignaMessageNode retry(String msgId, String sessionId, VignaMsg msg, Long clientID) {
        //检查是否满足插入条件
        if (msg == null || MyStringUtil.isNull(msgId))
            throw new OperationException("消息重试失败!因为必要信息为空!");
        String id = IdUtil.getRandomId("msg");
        //检查是否存在节点
        Optional<VignaMessageNode> lastMsg = neo4jTemplate.findById(msgId, VignaMessageNode.class);
        if (lastMsg.isEmpty())
            throw new OperationException("消息重试失败!因为前置信息不存在!");
        VignaMessageNode oldNode = lastMsg.get();
        VignaMessageNode node = new VignaMessageNode();
        node.setRole(msg.getRole().getCode());
        node.setContent(msg.getContent());
        node.setTurnIndex(msg.getIndex());
        node.setClientId(clientID);
        node.setLastMessage(oldNode.getLastMessage());
        node.setIsSummary(msg.getRole() == VignaRole.sum);
        node.setTimestamp(OffsetDateTime.now());
        VignaMessageNode node1 = setData(node, sessionId, true);
        oldNode.getRetriedVersions().add(node);
        neo4jTemplate.save(oldNode);
        return node1;
        
    }
    
    @Override
    @Transactional
    public VignaMessageNode setData(VignaMessageNode msg, String sessionId, boolean updateSession) {
        //检查是否满足插入条件
        if (msg == null || MyStringUtil.isNull(msg.getLastMessage().getMessageId()))
            throw new OperationException("消息保存失败!因为必要信息为空!");
        //检查是否存在节点(即使修改/回复的部分检查过了,但这个方法还会被单独调用因此保留)
        Optional<VignaMessageNode> lastMsg = neo4jTemplate.findById(msg.getLastMessage().getMessageId(), VignaMessageNode.class);
        if (lastMsg.isEmpty())
            throw new OperationException("消息保存失败!因为前置信息不存在!");
        //存在时直接插入并更新会话
        Optional<VignaSessionNode> sessionNode = neo4jTemplate.findById(sessionId, VignaSessionNode.class);
        if (sessionNode.isEmpty())
            throw new OperationException("消息保存失败!因为信息所属会话信息不存在!");
        VignaSessionNode session = sessionNode.get();
        //更新id
        String id = IdUtil.getRandomId("msg");
        session.setTailId(id);
        msg.setMessageId(id);
        msg.setSessionId(sessionId);
        msg.setLastMessage(lastMsg.get());
        if (updateSession)
            neo4jTemplate.save(session);
        return neo4jTemplate.save(msg);
    }
    
    @Override
    public VignaMessageNode getMsgNode(String msgId) {
        Optional<VignaMessageNode> byId = neo4jTemplate.findById(msgId, VignaMessageNode.class);
        return byId.orElse(null);
    }
}
