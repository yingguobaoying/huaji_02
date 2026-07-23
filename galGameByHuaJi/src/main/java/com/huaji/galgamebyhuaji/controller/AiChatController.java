package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.dao.AiRecordMapper;
import com.huaji.galgamebyhuaji.dto.ChatParam;
import com.huaji.galgamebyhuaji.entity.AiRecordWithBLOBs;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.AiChatClientParam;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.service.ai.AiBastService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class AiChatController extends BaseController {
    private final AiBastService chatService;
    private final AiRecordMapper aiRecordMapper;
    
    /**
     * 非流式 AI 对话
     */
    @PostMapping("/chat")
    public ReturnResult<String> chat(@Valid @RequestBody ChatParam chatParam, BindingResult testResult) {
        if (testResult.hasErrors()) {
            return ReturnResult.isFalse(testResult.getFieldError() != null ?
                                         testResult.getFieldError().getDefaultMessage() : "未知错误请稍后重试");
        }
        AiChatClientParam param = new AiChatClientParam();
        Users loginUser = getLoginUser();
        param.setUserId(loginUser.getUserId());
        param.setSessionId(chatParam.getSessionId());
        param.setUserContent(chatParam.getContent());
        param.setClientId(chatParam.getClientId());
        return chatService.aiChat(chatParam.getClientId(), null, param);
    }
    
    /**
     * 流式 AI 对话 (SSE)
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@Valid @RequestBody ChatParam chatParam, BindingResult testResult) {
        if (testResult.hasErrors()) {
            String errorMsg = testResult.getFieldError() != null
                    ? testResult.getFieldError().getDefaultMessage()
                    : "未知错误请稍后重试";
            return Flux.just("data: " + errorMsg + "\n\n");
        }
        AiChatClientParam param = new AiChatClientParam();
        Users loginUser = getLoginUser();
        param.setUserId(loginUser.getUserId());
        param.setSessionId(chatParam.getSessionId());
        param.setUserContent(chatParam.getContent());
        param.setClientId(chatParam.getClientId());
        return chatService.aiChatByStream(chatParam.getClientId(), null, param);
    }
    
    /**
     * 获取指定会话的聊天历史
     */
    @GetMapping("/chat/history/{sessionId}")
    public ReturnResult getChatHistory(@PathVariable("sessionId") String sessionId) {
        Users loginUser = getLoginUser();
        List<AiRecordWithBLOBs> records = aiRecordMapper.getRecord(
                loginUser.getUserId(), sessionId);
        if (records == null || records.isEmpty()) {
            return ReturnResult.isTrue("暂无聊天记录", List.of());
        }
        return ReturnResult.isTrue("聊天记录获取成功", records);
    }
    
    /**
     * 获取该用户所有会话的首条记录（用于显示会话列表）
     */
    @GetMapping("/chat/sessions")
    public ReturnResult getChatSessions() {
        Users loginUser = getLoginUser();
        List<AiRecordWithBLOBs> records = aiRecordMapper.getFirstRecord(loginUser.getUserId());
        if (records == null || records.isEmpty()) {
            return ReturnResult.isTrue("暂无会话", List.of());
        }
        return ReturnResult.isTrue("会话列表获取成功", records);
    }
    
}
