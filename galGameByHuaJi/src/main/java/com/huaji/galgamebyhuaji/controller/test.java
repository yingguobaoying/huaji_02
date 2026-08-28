package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
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
public class test {
    private final VignaAiChat chat;
    
    @GetMapping("/test")
    public void testChat() {
        VignaMsg vignaMsg = new VignaMsg();
        vignaMsg.setContent("测试消息,请回复你收到的条数和从早到晚所有消息的序号,本消息序号为[1]");
        vignaMsg.setRole(VignaRole.user);
        ReturnResult<String> stringReturnResult = chat.vignaAiChat(vignaMsg, "001", 0, 1, false);
        vignaMsg.setContent("测试消息,请回复你收到的条数和从早到晚所有消息的序号,本消息序号为[2]");
        ReturnResult<String> stringReturnResult1 = chat.vignaAiChat(vignaMsg, "001", 0, 1, false);
        vignaMsg.setContent("测试消息,请回复你收到的条数和从早到晚所有消息的序号,本消息序号为[3]");
        ReturnResult<String> stringReturnResult2 = chat.vignaAiChat(vignaMsg, "001", 0, 1, false);
        vignaMsg.setContent("测试消息,请回复你收到的条数和从早到晚所有消息的序号,本消息序号为[4]");
        ReturnResult<String> stringReturnResult3 = chat.vignaAiChat(vignaMsg, "001", 0, 1, false);
        vignaMsg.setContent("测试消息,请回复你收到的条数和从早到晚所有消息的序号,本消息序号为[5]");
        ReturnResult<String> stringReturnResult4 = chat.vignaAiChat(vignaMsg, "001", 0, 1, false);
        vignaMsg.setContent("测试消息,请回复你收到的条数和从早到晚所有消息的序号,本消息序号为[6]");
        ReturnResult<String> stringReturnResult5 = chat.vignaAiChat(vignaMsg, "001", 0, 1, false);
        System.out.println(stringReturnResult);
        System.out.println(stringReturnResult1);
        System.out.println(stringReturnResult2);
        System.out.println(stringReturnResult3);
        System.out.println(stringReturnResult4);
        System.out.println(stringReturnResult5);
    }
}
