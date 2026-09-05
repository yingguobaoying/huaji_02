package com.huaji.galgamebyhuaji.vignaAiFrame.filter;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsgContext;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaMsgType;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaMessageNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaSessionNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

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
        VignaMessageNode node;
        if (context.getType() == null) context.setType(VignaMsgType.generic);
        switch (context.getType()) {
            case edit ->
                    node = msgService.editData(context.getMsgId(), sessionId, context.getContent(), context.getClientId());
            case retry ->
                    node = msgService.retry(context.getMsgId(), sessionId, context.getContent(), context.getClientId());
            default -> {
                node = new VignaMessageNode();
                if (MyStringUtil.isNull(context.getMsgId())) {
                    //如果为空将上一条消息设置为默认链路的
                    Optional<VignaSessionNode> sessionNode = neo4jTemplate.findById(sessionId, VignaSessionNode.class);
                    if (sessionNode.isEmpty())
                        throw new OperationException("错误!会话不存在!");
                    VignaSessionNode vignaSessionNode = sessionNode.get();
                    if (!MyStringUtil.isNull(vignaSessionNode.getTailId())) {//存在时设置
                        Optional<VignaMessageNode> last = neo4jTemplate.findById(vignaSessionNode.getTailId(), VignaMessageNode.class);
                        if (last.isEmpty())
                            throw new OperationException("错误!消息前置对话不存在!");
                        node.setLastMessage(last.get());
                    }   //不存在说明是第一条不做处理
                } else {
                    Optional<VignaMessageNode> last = neo4jTemplate.findById(context.getMsgId(), VignaMessageNode.class);
                    if (last.isEmpty())
                        throw new OperationException("错误!消息前置对话不存在!");
                    node.setLastMessage(last.get());
                }
                node.setClientId(context.getClientId());
                node.setTimestamp(OffsetDateTime.now());
                node.setContent(context.getContent().getContent());
                node.setRole(context.getContent().getRole().getCode());
                node.setIsSummary(Boolean.TRUE.equals(context.isSum()));
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
        VignaMsg reply = context.getAiReply();
        if (reply == null)
            node.setTurnIndex(context.getContent().getIndex() + 1);
        else
            node.setTurnIndex(reply.getIndex());
        node.setRole(VignaRole.ai.getCode());
        node.setPromptRaw(context.getSystemMsg().getContent());
        node.setJson(context.getFinishReason());
        //设置关联链
        node.setLastMessage(msgNode);
        if (context.isError()) {
            node.setContent("ai响应出错" + context.getErrorMsg() + "!请稍后再试或者联系管理员处理");
            node.setError(true);
            VignaMessageNode vignaMessageNode = msgService.setData(node, sessionId, true);
            if (!context.isSum()) return;//如果是总结模式那就更新下上条消息id
            context.setMsgId(vignaMessageNode.getMessageId());
            ChatContextMap.setContext(sessionId, context);
            return;
        }
        //存放AI聊天记录
        node.setContent(reply == null ? "ai回复获取解析失败或者ai模型返回了空回复" : reply.getContent());
        node.setIsSummary(context.isSum());
        node.setError(false);
        VignaMessageNode vignaMessageNode = msgService.setData(node, sessionId, true);
        if (!context.isSum()) return;
        context.setMsgId(vignaMessageNode.getMessageId());
        ChatContextMap.setContext(sessionId, context);
        //习惯性的强制刷新避免不生效
    }
}
