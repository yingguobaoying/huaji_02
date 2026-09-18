package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.service.RootServlet;
import com.huaji.galgamebyhuaji.vignaAiFrame.config.VignaChatClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/user/refresh")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN_JURISDICTION')")
public class RefreshController {
    private final VignaChatClientConfig vignaChatClientConfig;
    private final RootServlet rootServlet;
    @GetMapping("/chatConfig")
    public ReturnResult<Void> chat(){
        vignaChatClientConfig.refresh();
        return ReturnResult.isTrue("ai聊天客户端配置刷新完成");
    }
    @GetMapping("/rootUser")
    public ReturnResult<Void> root() throws SessionExceptions {
        rootServlet.rootUserInit();
        return ReturnResult.isTrue("ai聊天客户端配置刷新完成");
    }
}
