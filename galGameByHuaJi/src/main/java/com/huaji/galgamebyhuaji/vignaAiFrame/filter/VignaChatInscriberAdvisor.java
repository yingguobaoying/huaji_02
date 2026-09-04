package com.huaji.galgamebyhuaji.vignaAiFrame.filter;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaMessageNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * 进行聊天记录记录,这个过滤器的职责:记录用户发送的消息+ai回复消息
 * 应该是最后执行的
 *
 * @author 滑稽
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class VignaChatInscriberAdvisor implements MyBaseAdvisor {
    private final AiChatMsgService msgService;
    private final Neo4jTemplate neo4jTemplate;
    
    @Override
    public int getIndex() {
        return Integer.MAX_VALUE;
    }
    
    @Transactional
    @Override
    public void beforeAdvise(String sessionId) {
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null) throw new OperationException("请求上下文不存在，可能已被提前清理");
        String id;
        VignaMessageNode node;
        switch (context.getType()) {
            case edit ->
                    node = msgService.editData(context.getMsgId(), sessionId, context.getContent(), context.getClientId());
            case retry ->
                    node = msgService.retry(context.getMsgId(), sessionId, context.getContent(), context.getClientId());
            default -> {
                node = new VignaMessageNode();
                node.setClientId(context.getClientId());
                node.setTimestamp(OffsetDateTime.now());
                node.setContent(context.getContent().getContent());
                node.setRole(context.getContent().getRole().getCode());
                node.setIsSummary(false);
                node.setModifiedVersions(null);
                node.setRetriedVersions(null);
                node.setSessionId(sessionId);
                node.setTurnIndex(context.getContent().getIndex());
                node.setPromptRaw(context.getSystemMsg().getContent());
                node = msgService.setData(node, sessionId, true);
            }
        }
        context.setId(node.getMessageId());
        ChatContextMap.setContext(sessionId, context);
    }
    
    @Override
    @Transactional
    public void afterAdvise(String sessionId) {
        VignaMsgContext context = ChatContextMap.getContext(sessionId);
        if (context == null) throw new OperationException("请求上下文不存在，可能已被提前清理");
        //更新ai回复+更新全部需要更新的玩意
        VignaMessageNode msgNode = msgService.getMsgNode(context.getId());
        if (msgNode == null) {
            log.warn("警告会话{}的消息{}不存在,已跳过AI回复记录和信息更新", sessionId, context.getId());
            return;
        }//更新用户对话JSON
           msgNode.setJson(context.getSendJson());
        neo4jTemplate.save(msgNode);
        VignaMessageNode node = new VignaMessageNode();
        node.setClientId(context.getClientId());
        node.setTimestamp(OffsetDateTime.now());
        node.setIsSummary(context.isSum());
        node.setModifiedVersions(null);
        node.setRetriedVersions(null);
        node.setSessionId(sessionId);
        node.setTurnIndex(context.getAiReply().getIndex());
        node.setRole(VignaRole.ai.getCode());
        node.setPromptRaw(context.getSystemMsg().getContent());
        node.setJson(context.getFinishReason());
        if (context.isError()) {
            node.setContent("ai响应出错" + context.getErrorMsg() + "!请稍后再试或者联系管理员处理");
            node.setError(true);
            msgService.setData(node,sessionId,true);
            return;
        }
        //存放AI聊天记录
        node.setContent(context.getAiReply().getContent());
        node.setIsSummary(context.isSum());
        node.setError(false);
        msgService.setData(node, sessionId, true);
    }
}
