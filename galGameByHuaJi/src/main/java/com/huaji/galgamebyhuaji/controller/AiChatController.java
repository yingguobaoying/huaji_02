package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.dto.VignaChatUserPram;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.UserWithVignaChat;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class AiChatController extends BaseController {
    private final UserWithVignaChat userChatMsgService;
    private final AiChatMsgService msgService;
    private final VignaAiChat chatService;
    private final VignaSessionService sessionService;
    
    @GetMapping("/getUserSessionList")
    public ReturnResult<Map<String, List<VignaMsg>>> gerUserSessionList() {
        Users loginUser = getLoginUser();
        return ReturnResult.isTrue("获取成功", userChatMsgService.getUserSession(loginUser.getUserId()));
    }
    
    @PostMapping("/getSessionDeftChatMsg/")
    public ReturnResult<VignaMsg> getSession(
            @RequestBody VignaChatUserPram pram
    ) {//获取某个会话的默认链
        List<VignaMsg> msgList = msgService.getMsgList(pram.getSessionId(), pram.getMsgId(), pram.getSize());
        return ReturnResult.isTrue("会话获取成功", msgList);
    }
    @PostMapping("/getAllBySession/")
    public ReturnResult<VignaMsg> getSessionList(
            @RequestBody VignaChatUserPram pram
    ) {//获取某个会话的默认链
        List<VignaMsg> msgList = msgService.getMsgList(pram.getSessionId(), pram.getMsgId(), pram.getSize());
        return ReturnResult.isTrue("会话获取成功", msgList);
    }
    
    @PostMapping("/chat")
    public ReturnResult<String> chat(
            @RequestBody VignaChatUserPram pram
    ) {//进行对话
        return null;
    }
    
    @PostMapping("/stream/chat")
    public Flux<String> chatByStream(
            @RequestBody VignaChatUserPram pram
    ) {
        return null;
    }
}
