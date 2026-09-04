package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.myUtil.IdUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaMessageNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaSessionNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.impl.AiChatMsgServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.OffsetDateTime;

@Controller
@ResponseBody
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class test {
    private final Neo4jTemplate neo4jTemplate;
    private final AiChatMsgServiceImpl service;
    
    private static final String SESSION_ID = "session-002";
    private static final Long CLIENT_ID = 1001L;
    
    @Transactional
    @GetMapping("/test")
    public void testMessageChain() {
        VignaSessionNode vignaSessionNode = new VignaSessionNode();
        vignaSessionNode.setSessionId(SESSION_ID);
        neo4jTemplate.save(vignaSessionNode);
        // 1. 用户输入 (turnIndex=1)
        VignaMsg userMsg1 = new VignaMsg(VignaRole.user, "你好，今天天气如何？", 1);
        VignaMessageNode node1 = createFirstMessage(userMsg1, SESSION_ID);
        // 2. AI回复1 (turnIndex=2)
        VignaMsg aiMsg1 = new VignaMsg(VignaRole.ai, "今天天气晴朗，适合出行。", 2);
        VignaMessageNode node2 = service.setData(buildNode(aiMsg1, node1), SESSION_ID, true);
        // 3. 系统以用户身份发送的总结消息 (角色为总结, turnIndex=3)
        VignaMsg sumMsg1 = new VignaMsg(VignaRole.sum, "用户询问天气，AI回答晴朗。", 3);
        VignaMessageNode node3 = service.setData(buildNode(sumMsg1, node2), SESSION_ID, true);
        // 4. AI回复3 (角色为AI，但isSummary=true, turnIndex=4)
        VignaMsg aiMsg2 = new VignaMsg(VignaRole.ai, "是的，今天非常适合户外活动。", 4);
        VignaMessageNode node4 = service.setData(buildNode(aiMsg2, node3), SESSION_ID, true);
        // 5. 用户输入 (turnIndex=5)
        VignaMsg userMsg2 = new VignaMsg(VignaRole.user, "那明天呢？", 5);
        VignaMessageNode node5 = service.setData(buildNode(userMsg2, node4), SESSION_ID, true);
        // 6. AI回复5 (turnIndex=6)
        VignaMsg aiMsg3 = new VignaMsg(VignaRole.ai, "明天预报有雨，记得带伞。", 6);
        VignaMessageNode node6 = service.setData(buildNode(aiMsg3, node5), SESSION_ID, true);
        // 7. 用户输入 (turnIndex=7)
        VignaMsg userMsg3 = new VignaMsg(VignaRole.user, "帮我查一下后天天气", 7);
        VignaMessageNode node7 = service.setData(buildNode(userMsg3, node6), SESSION_ID, true);
        // 8. AI回复7 (turnIndex=8)
        VignaMsg aiMsg4 = new VignaMsg(VignaRole.ai, "后天多云，温度适宜。", 8);
        VignaMessageNode node8 = service.setData(buildNode(aiMsg4, node7), SESSION_ID, true);
        // 9. 用户修改输入7 (修改node7，产生MODIFY分支)
        VignaMsg modifiedUserMsg = new VignaMsg(VignaRole.user, "帮我查一下后天和后天的后天天气", 7); // turnIndex保持7
        VignaMessageNode modifiedNode7 = service.editData(node7.getMessageId(), SESSION_ID, modifiedUserMsg, CLIENT_ID);
        // 10. AI回复9 (基于修改后的用户输入，turnIndex=10)
        VignaMsg aiMsg5 = new VignaMsg(VignaRole.ai, "后天多云，大后天晴转多云。", 10);
        VignaMessageNode node9 = service.setData(buildNode(aiMsg5, modifiedNode7), SESSION_ID, true);
        
        // 12. AI回复9 (重试，基于node9进行重试，产生RETRY分支，turnIndex保持10)
        VignaMsg retryAiMsg = new VignaMsg(VignaRole.ai, "后天多云转阴，大后天晴。", 10);
        VignaMessageNode retryNode9 = service.retry(node9.getMessageId(), SESSION_ID, retryAiMsg, CLIENT_ID);
        // 13. 用户输入 (turnIndex=13)
        VignaMsg userMsg4 = new VignaMsg(VignaRole.user, "好的，谢谢", 13);
        VignaMessageNode node10 = service.setData(buildNode(userMsg4, retryNode9), SESSION_ID, true);
        // 14. AI回复13 (turnIndex=14)
        VignaMsg aiMsg6 = new VignaMsg(VignaRole.ai, "不客气，祝您愉快！", 14);
        VignaMessageNode node11 = service.setData(buildNode(aiMsg6, node10), SESSION_ID, true);
    }
    
    // 辅助方法：创建第一条消息（无前置）
    private VignaMessageNode createFirstMessage(VignaMsg msg, String sessionId) {
        VignaMessageNode node = new VignaMessageNode();
        node.setRole(msg.getRole().getCode());
        node.setContent(msg.getContent());
        node.setTurnIndex(msg.getIndex());
        node.setClientId(CLIENT_ID);
        node.setIsSummary(msg.getRole() == VignaRole.sum);
        node.setTimestamp(OffsetDateTime.now());
        node.setSessionId(sessionId);
        node.setMessageId(IdUtil.getRandomId("msg"));  // 自行生成ID
        node.setLastMessage(null);  // 第一条无前置
        // 更新会话tailId（这里需手动处理，因为setData不会参与）
        VignaSessionNode session = neo4jTemplate.findById(sessionId, VignaSessionNode.class)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        session.setTailId(node.getMessageId());
        neo4jTemplate.save(session);
        return service.setData(node,sessionId,true);
    }
    
    // 辅助方法：构建待保存的节点（设置除lastMessage和ID外的属性）
    private VignaMessageNode buildNode(VignaMsg msg, VignaMessageNode previous) {
        VignaMessageNode node = new VignaMessageNode();
        node.setRole(msg.getRole().getCode());
        node.setContent(msg.getContent());
        node.setTurnIndex(msg.getIndex());
        node.setClientId(CLIENT_ID);
        node.setIsSummary(msg.getRole() == VignaRole.sum);
        node.setTimestamp(OffsetDateTime.now());
        node.setLastMessage(previous);  // 前置消息
        return node;
    }
}
