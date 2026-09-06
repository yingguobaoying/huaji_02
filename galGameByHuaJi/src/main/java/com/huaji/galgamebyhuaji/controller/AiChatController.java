package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.vignaAiFrame.message.VignaMsg;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.UserWithVignaChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class AiChatController extends BaseController {
    private final UserWithVignaChat userChatMsgService;
    @GetMapping("/getUserSessionList")
    ReturnResult<Map<String, List<VignaMsg>>> gerUserSessionList() {
        Users loginUser = getLoginUser();
        
        return ReturnResult.isTrue("", null);
    }
}
