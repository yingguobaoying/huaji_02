package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class test004 {
    final AiChatMsgServiceImpl aiChatMsgService;
    
    @GetMapping("/test/01")
    public void test() {
        aiChatMsgService.getMsgList("session-001", 0);
        aiChatMsgService.getMsgList(null, "msg-007", 0);
    }
}
