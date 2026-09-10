package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.config.VignaChatClientConfig;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaMessageNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaSessionNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.repository.MsgRepository;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.repository.SessionRepository;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiClassificationServlet;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.UserWithVignaChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.NamedPath;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Statement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserWithVignaChatImpl implements UserWithVignaChat {
    private final AiClassificationServlet classificationServlet;
    private final MsgRepository msgRepository;
    private final SessionRepository sessionRepository;
    
    @Override
    public List<AiClientConfigWithBLOBs> getList(int userId) {
        List<AiClientConfigWithBLOBs> list = classificationServlet.getList(userId);
        if (ListUtil.isNull(list))
            return List.of();
        else//选出实际可以使用的
            return list.stream().filter(c -> VignaChatClientConfig.idState(c.getId())).toList();
    }
    
    @Override
    public Map<String, List<VignaMsg>> getUserSession(int userId) {
        Node session = Cypher.node("VignaSession").named("s");
        Node message = Cypher.node("VignaMessage").named("n");
        Node previous = Cypher.node("VignaMessage");
        /*获取用户全部的会话信息
        MATCH (s:VignaSession) WHERE s.userId =1
            RETURN s
         */
        Statement statement = Cypher
                .match(session)
                .where(session.property("sessionId")
                               .isEqualTo(Cypher.parameter("userId", userId)))
                .returning("s").build();
        Collection<VignaSessionNode> sessionNodes = sessionRepository.findAll(statement);
        if (sessionNodes.isEmpty()) return Map.of();
        /*根据用户id获取全部会话根节点
        MATCH (s:VignaSession) WHERE s.userId =1
        MATCH (n:VignaMessage) WHERE NOT ((n)-[:LAST]->())and n.sessionId=s.sessionId RETURN n
         */
        NamedPath path = Cypher.path("path").definedBy(
                message.relationshipTo(previous, "LAST").min(0));
        statement = Cypher
                .match(session)
                .where(session.property("sessionId")
                               .isEqualTo(Cypher.parameter("userId", userId)))
                .match(path)
                .where(Cypher.not(message.relationshipTo(Cypher.anyNode(), "LAST")))
                .and(message.property("sessionId")
                             .isEqualTo(session.property("sessionId")))
                .returning("n").build();
        Collection<VignaMessageNode> msgNode = msgRepository.findAll(statement);
        Map<String, List<VignaMsg>> r = new HashMap<>(sessionNodes.size());
        for (VignaSessionNode node : sessionNodes) r.put(node.getSessionId(), new ArrayList<>());
        if (msgNode.isEmpty()) return r;
        msgNode.forEach(m->{
            List<VignaMsg> vignaMsgList = r.get(m.getSessionId());
            if(ListUtil.isNull(vignaMsgList))
                vignaMsgList = new ArrayList<>(16);
            vignaMsgList.add(new VignaMsg(m));
            r.put(m.getSessionId(), vignaMsgList);
        });
        return r;
    }
}
