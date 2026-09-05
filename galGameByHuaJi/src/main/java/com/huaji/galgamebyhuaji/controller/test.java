package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatServicePara;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaMsgType;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaSessionNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.OffsetDateTime;
import java.util.Optional;

@Controller
@ResponseBody
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class test {
    private final Neo4jTemplate neo4jTemplate;
    private final VignaAiChat service;
    
    private static final String SESSION_ID = "session-002";
    private static final Long CLIENT_ID = 1L;
    
    @GetMapping("/test")
    public void testMessageChain() {
        VignaSessionNode vignaSessionNode = new VignaSessionNode();
        vignaSessionNode.setSessionId(SESSION_ID);
        vignaSessionNode.setTailId(null);
        vignaSessionNode.setUserId(1);
        vignaSessionNode.setCreatedAt(OffsetDateTime.now());
        neo4jTemplate.save(vignaSessionNode);
        ChatServicePara para = new ChatServicePara();
        para.setClientId(CLIENT_ID);
        para.setSessionId(SESSION_ID);
        para.setType(VignaMsgType.generic);
        VignaMsg vignaMsg = new VignaMsg();
        vignaMsg.setRole(VignaRole.user);
        vignaMsg.setContent("测试信息,本信息序号为[1],收到请返回你收到的全部信息的序号,以及你回复的序号(用'进行标记),排序方式为[早]->[晚]");
        para.setMsg(vignaMsg);
        Optional<VignaSessionNode> byId = neo4jTemplate.findById(SESSION_ID, VignaSessionNode.class);
        para.setMsgId(byId.get().getTailId());
        System.out.println(service.vignaAiChat(para));
        vignaMsg.setContent("测试信息,本信息序号为[2],收到请返回你收到的全部信息的序号,以及你回复的序号(用'进行标记),排序方式为[早]->[晚]");
        byId = neo4jTemplate.findById(SESSION_ID, VignaSessionNode.class);
        para.setMsgId(byId.get().getTailId());
        para.setMsg(vignaMsg);
        System.out.println(service.vignaAiChat(para));
        vignaMsg.setContent("测试信息,本信息序号为[3],收到请返回你收到的全部信息的序号,以及你回复的序号(用'进行标记),排序方式为[早]->[晚]");
        byId = neo4jTemplate.findById(SESSION_ID, VignaSessionNode.class);
        para.setMsgId(byId.get().getTailId());
        para.setMsg(vignaMsg);
        System.out.println(service.vignaAiChat(para));
    }
}
