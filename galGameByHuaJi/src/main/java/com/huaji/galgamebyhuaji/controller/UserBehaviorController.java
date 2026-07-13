package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dto.UserMxgWithOldUserMxg;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.huaji.galgamebyhuaji.myUtil.ElseUtil.getToken;

@Controller
@RequestMapping("/api/user")
@ResponseBody
@RequiredArgsConstructor
public class UserBehaviorController extends BaseController {
    final UserMxgServlet userMxgServlet;
    final TokenService tokenService;
    
    
    @GetMapping("/getUserMxg")
    public ReturnResult<UsersWithBLOBs> getUserMxg() {
        Users loginUser = getLoginUser();
        return ReturnResult.isTrue("用户信息获取成功!", userMxgServlet.getItselfMxg(loginUser.getUserId()));
    }
    
    @PostMapping("/updateUserMxg")
    public ReturnResult<UsersWithBLOBs> updateUserMxg(
            @RequestBody UserMxgWithOldUserMxg userMxgWithOldUserMxg,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ReturnResult.isFalse(bindingResult.getAllErrors().getFirst().getDefaultMessage());
        }
        Users loginUser = getLoginUser();
        //拒绝前端使用的,使用令牌提供的
        userMxgWithOldUserMxg.getUsers().setUserId(loginUser.getUserId());
        return GlobalLock.safeExecute(loginUser.getUserId(), () -> {
            UsersWithBLOBs usersWithBLOBs = userMxgServlet.updateUsers(userMxgWithOldUserMxg);
            return ReturnResult.isTrue("用户信息更新成功!", usersWithBLOBs);
        });
    }
    
    @PostMapping("/setUserMxgOfHead")
    public ReturnResult<String> setUserMxgOfHead(
            @RequestParam("croppedImage") MultipartFile file,
            @RequestParam(value = "banHeadPortrait", required = false) Boolean banHeadPortrait,
            HttpServletRequest request) throws BestException, IOException {
        if (banHeadPortrait == null)
            banHeadPortrait = false;
        Users loginUser = getLoginUser();
        String token = getToken(request);
        //再次校验token
        tokenService.verifyToken(token, loginUser.getUserId(), ElseUtil.getClientIp(request), false);
        return ReturnResult.isTrue("用户头像更新成功!", userMxgServlet.updateUserHeadPortraitUrl(file, loginUser.getUserId(), banHeadPortrait));
    }
    
}
