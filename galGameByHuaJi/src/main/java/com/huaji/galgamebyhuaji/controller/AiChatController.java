package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dto.VignaChatUserPram;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.model.ChatServicePara;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaMsgType;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.VignaRole;
import com.huaji.galgamebyhuaji.vignaAiFrame.node.VignaSessionNode;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiChatMsgService;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiClassificationServlet;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.UserWithVignaChat;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaAiChat;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaSessionService;
import com.huaji.galgamebyhuaji.vignaAiFrame.vo.VignaMsgTree;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Controller
@ResponseBody
@RequestMapping("/api/user/chat")
@RequiredArgsConstructor
@Slf4j
public class AiChatController extends BaseController {
    private final UserWithVignaChat userChatMsgService;
    private final AiChatMsgService msgService;
    private final VignaAiChat chatService;
    private final VignaSessionService sessionService;
    private final AiClassificationServlet classificationServlet;
    
    @GetMapping("/getUserSessionList")//1
    public ReturnResult<Map<String, List<VignaMsg>>> gerUserSessionList() {
        Users loginUser = getLoginUser();
        return ReturnResult.isTrue("获取成功", userChatMsgService.getUserSession(loginUser.getUserId()));
    }
    
    @PostMapping("/getSessionDeftChatMsg")//1
    public ReturnResult<VignaMsg> getSession(
            @RequestBody VignaChatUserPram pram
    ) {//获取某个会话的默认链
        sessionService.testSessionUser(pram.getSessionId(), getLoginUser().getUserId());
        if (pram.getSize() <= 0) pram.setSize(1);
        List<VignaMsg> msgList = msgService.getMsgList(pram.getSessionId(), null, pram.getSize());
        return ReturnResult.isTrue("会话获取成功", msgList);
    }
    
    @PostMapping("/getAllBySession")
    public ReturnResult<VignaMsg> getSessionList(
            @RequestBody VignaChatUserPram pram
    ) {//获取某个会话的默认链
        sessionService.testSessionUser(pram.getSessionId(), getLoginUser().getUserId());
        if (pram.getSize() <= 0) pram.setSize(1);
        List<VignaMsg> msgList = msgService.getMsgList(pram.getSessionId(), pram.getMsgId(), pram.getSize());
        return ReturnResult.isTrue("会话获取成功", msgList);
    }
    
    @PostMapping("/chat")
    public ReturnResult<String> chat(
            @Valid @RequestBody VignaChatUserPram pram
            , BindingResult testResult) {
        if (testResult.hasErrors())
            return ReturnResult.isFalse(testResult.getFieldError() ==
                                        null ? "未知错误请稍后再试" : testResult.getFieldError().getDefaultMessage());
        VignaSessionNode node = sessionService.testSessionUser(pram.getSessionId(), getLoginUser().getUserId());
        //进行对话
        return vignaChatLock(() -> {
            if (classificationServlet.userCanSee(getLoginUser().getUserId(), node.getConfigId())) {
                pram.setClientId(node.getConfigId());
                return chatService.vignaAiChat(getPara(pram));
            }
            return ReturnResult.isFalse("请求失败!因为您没有访问该模型的权限");
        }, pram.getSessionId());
    }
    
    
    @PostMapping("/stream/chat")
    public Flux<String> chatByStream(
            @Valid @RequestBody VignaChatUserPram pram
            , BindingResult testResult) {
        if (testResult.hasErrors())
            throw new OperationException(
                    testResult.getFieldError() ==
                    null ? "未知错误请稍后再试" : testResult.getFieldError().getDefaultMessage());
        VignaSessionNode node = sessionService.testSessionUser(pram.getSessionId(), getLoginUser().getUserId());
        String sessionId = pram.getSessionId();
        return GlobalLock.safeExecute(getLoginUser().getUserId(), () -> {
            if (!classificationServlet.userCanSee(getLoginUser().getUserId(), node.getConfigId()))
                throw new OperationException("请求失败!因为您没有访问该模型的权限");
            int i = sessionService.sessionLeisure(sessionId);
            if (i == 2)
                throw new OperationException("对话失败,因为当前会话正在被使用!");
            else if (i == 0) {
                throw new OperationException("对话失败,因为当前会话不存在!");
            }
            sessionService.lockSession(sessionId);
            pram.setClientId(node.getConfigId());
            Flux<String> flux = chatService.vignaAiChatByStream(getPara(pram));
            return flux.doAfterTerminate(() -> sessionService.unlockSession(sessionId));
        });
    }
    
    private ChatServicePara getPara(@RequestBody @Valid VignaChatUserPram pram) {
        ChatServicePara para = new ChatServicePara();
        para.setClientId(pram.getClientId());
        para.setSessionId(pram.getSessionId());
        VignaMsg vignaMsg = new VignaMsg();
        vignaMsg.setContent(pram.getContent());
        vignaMsg.setRole(VignaRole.user);
        para.setMsgId(pram.getMsgId());
        para.setMsg(vignaMsg);
        para.setUserId(getLoginUser().getUserId());
        para.setSumUp(false);
        para.setType(VignaMsgType.get(pram.getType()));
        return para;
    }
    
    private <T> T vignaChatLock(Supplier<T> action, String sessionId) {
        return GlobalLock.safeExecute(getLoginUser().getUserId(), () -> {
            int i = sessionService.sessionLeisure(sessionId);
            if (i == 2)
                throw new OperationException("对话失败,因为当前会话正在被使用!");
            else if (i == 0) {
                throw new OperationException("对话失败,因为当前会话不存在!");
            }
            sessionService.lockSession(sessionId);
            try {
                return action.get();
            } finally {
                sessionService.unlockSession(sessionId);
            }
        });
    }
    
    @GetMapping("/session/{clientId}")
    public ReturnResult<String> getSession(@PathVariable long clientId) {
        Users loginUser = getLoginUser();
        String sessionId = sessionService.getSessionId(loginUser.getUserId(), clientId);
        return ReturnResult.isTrue("获取成功", sessionId);
    }
    
    @GetMapping("/session/get/{sessionId}")
    public ReturnResult<Void> getSessionState(@PathVariable String sessionId) {
        sessionService.testSessionUser(sessionId, getLoginUser().getUserId());
        return switch (sessionService.sessionLeisure(sessionId)) {//0:不存在的会话 1:空闲 2:占用
            case 1 -> ReturnResult.isTrue("当前会话可用", null);
            case 2 -> ReturnResult.isFalse("当前会话正在被使用,请稍后再试");
            default -> ReturnResult.isFalse("当前会话不存在,请新建");
        };
    }
    
    @GetMapping("/session/get/tree/{sessionId}")
    public ReturnResult<VignaMsgTree> getTree(@PathVariable String sessionId) {
        return ReturnResult.isTrue("聊天树获取成功", msgService.getTree(sessionId));
    }
    
    @PostMapping("/msg/get")
    public ReturnResult<VignaMsg> getMsgById(@RequestBody List<String> MsgIdList) {
        return ReturnResult.isTrue("消息获取成功", msgService.getMsgByIds(MsgIdList));
    }
}
