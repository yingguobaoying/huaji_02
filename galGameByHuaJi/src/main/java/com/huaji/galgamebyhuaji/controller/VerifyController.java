package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.LongTextConstant;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.model.jwtToken.VerifyEmail;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.EmailService;
import com.huaji.galgamebyhuaji.service.SecureServlet;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.impl.UserMxgServletImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class VerifyController extends BaseController {
    final EmailService emailService;
    final SecureServlet secureServlet;
    final TokenService tokenService;
    final UserMxgServletImpl userMxgServletImpl;
    final UsersMapper usersMapper;
    private final static String BASE_URL = "http://localhost:8080/MyGalGameByHuaJi/api/";
    
    //验证邮箱
    @GetMapping("/user/getVerifyEmail")
    @ResponseBody
    public ReturnResult<String> getVerifyEmail(HttpServletRequest request) throws SessionExceptions {
        Users loginUser = getLoginUser();
        long l = Constant.VERIFY_EMAIL_VALID_TIME / 1000 / 60 / 60;
        //验证token
        String token;
        loginUser = userMxgServletImpl.getItselfMxg(loginUser.getUserId());
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setIp(ElseUtil.getClientIp(request));
        verifyEmail.setUserId(loginUser.getUserId());
        verifyEmail.setEmail(loginUser.getMailbox());
        UserToken userToken = tokenService.insertToken(verifyEmail, TokenType.VERIFY_EMAIL, Constant.VERIFY_EMAIL_VALID_TIME);
        token = userToken.getToken();
        String sb = BASE_URL +
                    "verifyEmail?token=" +
                    token +
                    "&userId=" +
                    loginUser.getUserId();
        String content =
                "您好，您正在使用邮箱 %s 注册'因果报应的破小站'的账户。请点击下方按钮完成邮箱验证，验证链接有效期为 %d 小时。".formatted(
                        loginUser.getMailbox(), l);
        String eml = LongTextConstant.getEmailText(
                "邮箱验证通知",
                content,
                sb,
                emailService.getFROM_EMAIL()
        );
        emailService.sendEmail(loginUser.getMailbox(), eml, "验证您的邮箱", true);
        return ReturnResult.isTrue(
                "验证邮件已发送",
                "我们已向您的邮箱 %s 发送了一封验证邮件，有效期为 %d 小时。\n如果未收到邮件，请检查垃圾邮件或稍后重试。".formatted(
                        loginUser.getMailbox(), l
                )
        );
    }
    
    @GetMapping(value = "/verifyEmail", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String verifyEmail(
            @RequestParam("token") String token,
            @RequestParam("userId") int userId,
            HttpServletRequest request
    ) {
        if (MyStringUtil.isNull(token)) {
            return
                    LongTextConstant.getReturnPage("操作失败",
                                                   "操作失败了,请重新进行一次邮件验证,因为您未提供令牌信息");
        }
        String ip = ElseUtil.getClientIp(request);
        String string;
        boolean isTrue = false;
        String msg = null;
        try {
            msg = secureServlet.authenticationEmail(ip, userId, token);
            isTrue = true;
        } catch (OperationException oe) {
            msg = oe.getMsg();
        } catch (SessionExceptions e) {
            ErrorEnum errorEnum = ErrorEnum.SESSION_NOT_AVAILABLE_ERROR;
            if (errorEnum.getError_num() == e.getErrorNum() &&
                errorEnum.getError_type() == errorEnum.getError_type()) {
                msg = "您的账号当前不在线,因为我们验证邮件时需要您的账号在线,请您重新登录之后再次操作!";
            } else {
                msg = e.getMessage();
            }
        } catch (Exception e) {
            MyLogUtil.error(SecureServlet.class, e);
        }
        if (isTrue) {
            string = LongTextConstant.getReturnPage("操作成功", msg + ",您现在可以正常使用我们的小站了");
        } else {
            if (!MyStringUtil.isNull(msg)) {
                string = LongTextConstant.getReturnPage("操作失败",
                                                        "出错了,请重新进行一次邮件验证,错误原因是:" + msg);
            } else {
                string = LongTextConstant.getReturnPage("操作失败",
                                                        "服务器出错了,请重新进行一次邮件验证");
            }
        }
        return string;
        
    }
    
    @GetMapping("/user/verify/getLevel")
    @ResponseBody
    public ReturnResult<Integer> getLevel() {
        Users loginUser = getLoginUser();
        loginUser = usersMapper.selectByPrimaryKey(loginUser.getUserId());
        return ReturnResult.isTrue("权限信息获取成功", JurisdictionLevel.getJurisdiction(loginUser.getJurisdiction()).getLevel());
    }
}
