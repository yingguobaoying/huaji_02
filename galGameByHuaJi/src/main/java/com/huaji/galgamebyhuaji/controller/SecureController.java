package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.LongTextConstant;
import com.huaji.galgamebyhuaji.dto.LostPasswordUserMsg;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.UserException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.EmailService;
import com.huaji.galgamebyhuaji.service.SecureServlet;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/api/login")
@RequiredArgsConstructor
@ResponseBody
public class SecureController {
	private final EmailService emailService;
	private final SecureServlet secureServlet;
	private final UserMxgServlet userMxgServlet;
	
	//就参数我就懒得新建一个接收类了
	@PostMapping("/lostPassword")
	@Transactional
	public ReturnResult<String> lostPassword(@RequestBody Map<String, String> emailJson, HttpServletRequest request) throws UserException, SessionExceptions {
		String content =
				"您好，您正在使用重置您在 '因果报应的破小站' 账户的密码。请点击下方按钮复制您的验证令牌到剪贴板,然后贴到令牌确认输入框中.此令牌的有效期时间为 %d 小时。".formatted(
						Constant.VERIFY_EMAIL_VALID_TIME / 1000 / 60 / 60);
		String token;
		String email = emailJson.get("email");
		if (MyStringUtil.isNull(email))
			return ReturnResult.isFalse("邮箱不可为空");
		token = secureServlet.forgetPassword(email, ElseUtil.getClientIp(request));
		String emailText = LongTextConstant.getEmailCopyText("重置您的密码", content, email, token);
		emailService.sendEmail(email, emailText, "重置您的密码", true);
		return ReturnResult.isTrue(
				"验证邮件已发送",
				"我们已向您的邮箱 %s 发送了一封验证邮件，有效期为 %d 小时。\n如果未收到邮件，请检查垃圾邮件或稍后重试。".formatted(
						email, Constant.VERIFY_EMAIL_VALID_TIME / 1000 / 60 / 60
				)
		);
	}
	
	@PostMapping("/lostPassword/verify")
	public ReturnResult<String> changPassword(@Valid @RequestBody LostPasswordUserMsg userMsg, BindingResult testResult, HttpServletRequest request) throws BestException {
		if (testResult.hasErrors()) {
			if (testResult.getFieldError() != null)
				return ReturnResult.isFalse(testResult.getFieldError().getDefaultMessage());
			else
				return ReturnResult.isFalse("未知错误，请稍后重试。");
		}
		String string = secureServlet.changePassword(userMsg.getToken(), ElseUtil.getClientIp(request), userMsg.getNewPassword());
		return ReturnResult.isTrue("密码修改成功!", string);
	}
	
	@PostMapping("/frozenUser/sendEmail")
	public ReturnResult<String> sendEmailFrozenUser(@RequestBody Map<String, String> emailJson, HttpServletRequest request) throws SessionExceptions {
		String email = emailJson.get("email");
		return ReturnResult.isTrue(
				"验证邮件已发送",
				secureServlet.getFrozenUser(email)
		
		);
	}
	
	@PostMapping("/frozenUser/verify")
	public ReturnResult<String> verifyFrozenUser(@RequestBody Map<String, String> emailJson, HttpServletRequest request) throws SessionExceptions {
		String token = emailJson.get("token");
		if (MyStringUtil.isNull(token))
			return ReturnResult.isFalse("错误!未发现令牌");
		return ReturnResult.isTrue(
				"操作成功",
				secureServlet.frozenUser(-1, token)
		);
	}
	
	@PostMapping("/unfrozenUser/sendEmail")
	public ReturnResult<String> sendEmailUnfrozenUser(@RequestBody Map<String, String> emailJson, HttpServletRequest request) throws SessionExceptions {
		String email = emailJson.get("email");
		String newPassword = emailJson.get("newPassword");
		if (MyStringUtil.isNull(newPassword))
			return ReturnResult.isFalse("请输入新的密码");
		if (newPassword.length() < 6)
			return ReturnResult.isFalse("请输入密码至少为6位数");
		return ReturnResult.isTrue(
				"验证邮件已发送",
				secureServlet.getUnfrozenUser(email, newPassword)
		
		);
	}
	
	@PostMapping("/unfrozenUser/verify")
	public ReturnResult<String> verifyUnfrozenUser(@RequestBody Map<String, String> emailJson, HttpServletRequest request) throws SessionExceptions {
		String token = emailJson.get("token");
		if (MyStringUtil.isNull(token))
			return ReturnResult.isFalse("错误!未发现令牌");
		return ReturnResult.isTrue(
				"操作成功",
				secureServlet.unfrozenUser(-1, token, ElseUtil.getClientIp(request))
		);
	}
	
}
