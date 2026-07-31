package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.entity.Feedback;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import com.huaji.galgamebyhuaji.enumPackage.UserStatus;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.LogUtil;
import com.huaji.galgamebyhuaji.myUtil.PageUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.RootServlet;
import com.huaji.galgamebyhuaji.service.SecureServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/user/manage")
@Slf4j
@RequiredArgsConstructor
@ResponseBody
public class UserManageController extends BaseController {
    final LoginService loginService;
    private final RootServlet rootServlet;
    private final SecureServlet secureServlet;
    
    private Users getUser() {
        Users user = getLoginUser();
        if (!loginService.userHasJurisdiction(user.getUserId(), JurisdictionLevel.ADMIN_JURISDICTION))
            throw new OperationException("权限不足,如需进行操作请联系管理员");
        return user;
    }
    
    @GetMapping("/getUserList/{page}")
    @PreAuthorize("hasRole('ADMIN_JURISDICTION')")
    public ReturnResult<Users> getUserList(@PathVariable("page") int page) {
        Users user = getUser();
        LogUtil.RootBehaviorLog(log, "获取用户列表,当前页数{%s}".formatted(page), user);
        return ReturnResult.isTrue("用户列表获取成功", rootServlet.getUserList(new PageUtil(10, page)), null);
    }
    
    
    @GetMapping("/getUserSize")
    @PreAuthorize("hasRole('ADMIN_JURISDICTION')")
    public ReturnResult<Integer> getUserSize() {
        Users user = getUser();
        LogUtil.RootBehaviorLog(log, "获取用户总数量", user);
        return ReturnResult.isTrue("用户数目获取成功", rootServlet.getUserSize());
    }
    
    
    @GetMapping("/getUserInfo/{id}")
    @PreAuthorize("hasRole('ADMIN_JURISDICTION')")
    public ReturnResult<UsersWithBLOBs> getUserInfo(@PathVariable("id") int id) {
        Users user = getUser();
        LogUtil.RootBehaviorLog(log, "获取用户信息,用户id{%s}".formatted(id), user);
        return ReturnResult.isTrue("用户信息获取成功", rootServlet.RootSelectUserById(id, user.getUserId()));
    }
    
    /**
     * 管理员编辑用户信息
     */
    @PostMapping("/editUser")
    @PreAuthorize("hasRole('ADMIN_JURISDICTION')")
    public ReturnResult<String> editUser(@RequestBody UsersWithBLOBs users) {
        Users root = getUser();
        if (users.getUserId() == null || users.getUserId() < 0)
            return ReturnResult.isFalse("错误!未提供有效的用户ID");
        LogUtil.RootBehaviorLog(log, "尝试修改用户信息,被修改用户id{%s}".formatted(users.getUserId()), root);
        String result = rootServlet.RootEditUserMxg(users, root.getUserId());
        return ReturnResult.isTrue("用户信息修改完成", result);
    }
    
    /**
     * 管理员更改用户状态（封禁/解封/冻结）
     */
    @PostMapping("/updateUserStatus")
    @PreAuthorize("hasRole('ADMIN_JURISDICTION')")
    public ReturnResult<String> updateUserStatus(@RequestBody Map<String, Object> body) {
        Users root = getUser();
        Object userIdObj = body.get("userId");
        Object statusObj = body.get("status");
        if (userIdObj == null || statusObj == null)
            return ReturnResult.isFalse("错误!请提供userId和status参数");
        int userId;
        try {
            userId = Integer.parseInt(userIdObj.toString());
        } catch (NumberFormatException e) {
            return ReturnResult.isFalse("错误!请提供有效的用户ID");
        }
        UserStatus userStatus = UserStatus.testEnumValue(statusObj.toString());
        LogUtil.RootBehaviorLog(log, "尝试修改用户{%s}的状态为{%s}".formatted(userId, userStatus.getName()), root);
        String result = rootServlet.RootUpdateUSerStatus(userId, root.getUserId(), userStatus);
        return ReturnResult.isTrue("用户状态修改完成", result);
    }
    
    /**
     * 管理员按用户名/登录名搜索用户
     */
    @GetMapping("/searchUser/{name}")
    @PreAuthorize("hasRole('ADMIN_JURISDICTION')")
    public ReturnResult<UsersWithBLOBs> searchUser(@PathVariable("name") String name) {
        Users root = getUser();
        LogUtil.RootBehaviorLog(log, "搜索用户,关键词{%s}".formatted(name), root);
        return ReturnResult.isTrue("搜索完成", rootServlet.RootSelectUserByName(name, root.getUserId()), null);
    }
    
    /**
     * 管理员冻结指定用户
     */
    @PostMapping("/frozenUser/{userId}")
    @PreAuthorize("hasRole('ADMIN_JURISDICTION')")
    public ReturnResult<String> frozenUser(@PathVariable("userId") int userId, HttpServletRequest request) throws Exception {
        Users root = getUser();
        if (userId < 0)
            return ReturnResult.isFalse("错误!请提供有效的用户ID");
        String adminToken = ElseUtil.getToken(request);
        LogUtil.RootBehaviorLog(log, "冻结用户{%s}".formatted(userId), root);
        String result = secureServlet.frozenUser(userId, adminToken);
        return ReturnResult.isTrue("用户冻结操作完成", result);
    }
    
    /**
     * 用户提交反馈
     */
    @PostMapping("/feedback")
    @PreAuthorize("hasRole('NOT_VALIDATED')")
    public ReturnResult<String> feedback(@RequestBody Feedback feedback) {
        Users loginUser = getLoginUser();
        if (feedback.getText() == null || feedback.getText().isBlank())
            return ReturnResult.isFalse("反馈内容不可为空");
        feedback.setuId(loginUser.getUserId());
        secureServlet.feedback(feedback);
        return ReturnResult.isTrue("反馈提交成功,感谢您的反馈!", "反馈已提交");
    }
    
    /**
     * 管理员解冻指定用户
     */
    @PostMapping("/unfrozenUser/{userId}")
    @PreAuthorize("hasRole('ADMIN_JURISDICTION')")
    public ReturnResult<String> unfrozenUser(@PathVariable("userId") int userId, HttpServletRequest request) throws Exception {
        Users root = getUser();
        if (userId < 0)
            return ReturnResult.isFalse("错误!请提供有效的用户ID");
        String adminToken = ElseUtil.getToken(request);
        String clientIp = ElseUtil.getClientIp(request);
        LogUtil.RootBehaviorLog(log, "解冻用户{%s}".formatted(userId), root);
        String result = secureServlet.unfrozenUser(userId, adminToken, clientIp);
        return ReturnResult.isTrue("用户解冻操作完成", result);
    }
    
}
