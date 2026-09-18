package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.myUtil.IdUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaSessionNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.repository.SessionRepository;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Statement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VignaSessionServiceImpl implements VignaSessionService {
    private final AiChatMsgService chatMsgService;
    private final SessionRepository sessionRepository;
    // 使用 ConcurrentHashMap 维护会话状态
    // TRUE = 空闲, FALSE = 占用
    private final ConcurrentHashMap<String, Boolean> sessionContext = new ConcurrentHashMap<>(50);
    
    @Override
    public int sessionLeisure(String sessionId) {
        boolean hasSession = chatMsgService.hasSession(sessionId);
        if (!hasSession) return 0;
        
        Boolean status = sessionContext.get(sessionId);
        // null 或 TRUE 都视为空闲
        return Boolean.FALSE.equals(status) ? 2 : 1;
    }
    
    @Override
    @Transactional(transactionManager = "neo4jTransactionManager")
    public String getSessionId(int userId, long clientId) {
        String id = IdUtil.getRandomId("vigna_chat");
        VignaSessionNode node = new VignaSessionNode();
        node.setUserId(userId);
        node.setCreatedAt(OffsetDateTime.now());
        node.setTailId(null);
        node.setSessionId(id);
        node.setConfigId(clientId);
        sessionRepository.save(node);
        return id;
    }
    
    @Override
    public Set<String> getUserSessionId(int userId) {
        Node session = Cypher.node("VignaSession").named("s");
        Statement statement =
                Cypher.match(session)
                        .where(session.property("userId").isEqualTo(Cypher.parameter("userId", userId)))
                        .returning(Cypher.name("s"))
                        .build();
        Collection<VignaSessionNode> nodes = sessionRepository.findAll(statement);
        if (nodes.isEmpty()) return Set.of();
        return nodes.stream().map(VignaSessionNode::getSessionId).collect(Collectors.toSet());
    }
    
    @Override
    public void lockSession(String sessionId) {
        // 只有当 key 不存在，或者存在但值为 TRUE 时，才能成功放入 FALSE
        Boolean previous = sessionContext.putIfAbsent(sessionId, Boolean.FALSE);
        // 如果之前已经存在且为 FALSE，说明被占用
        if (Boolean.FALSE.equals(previous)) throw new OperationException("调用失败，此会话正在处理中，请勿重复提交");
    }
    
    @Override
    public void unlockSession(String sessionId) {
        sessionContext.remove(sessionId, Boolean.FALSE);
    }
    
    @Override
    public VignaSessionNode testSessionUser(String sessionId, int user) {
        if (MyStringUtil.isNull(sessionId)) throw new OperationException("会话获取失败,因为会话信息不存在");
        Optional<VignaSessionNode> session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) throw new OperationException("会话获取失败,因为会话信息不存在");
        if (session.get().getUserId() != user) throw new OperationException("会话获取失败,因为该会话不属于您");
        return session.get();
    }
}
