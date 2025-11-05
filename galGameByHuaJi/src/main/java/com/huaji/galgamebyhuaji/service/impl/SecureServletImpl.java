package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.LongTextConstant;
import com.huaji.galgamebyhuaji.controller.SecureController;
import com.huaji.galgamebyhuaji.dao.FeedbackMapper;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.*;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.enumPackage.UserStatus;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.UserException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.jwtToken.FrozenUser;
import com.huaji.galgamebyhuaji.model.jwtToken.LostPasswordUser;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.model.jwtToken.UnfrozenUser;
import com.huaji.galgamebyhuaji.model.jwtToken.VerifyEmail;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import com.huaji.galgamebyhuaji.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SecureServletImpl implements SecureServlet {
	final
	UsersMapper usersMapper;
	final
	UserMxgServlet userMxgServlet;
	final
	TokenService tokenService;
	final
	FeedbackMapper feedbackMapper;
	final
	SessionService sessionService;
	final
	PasswordEncryptionUtil passwordEncryptionUtil;
	final
	LoginService loginService;
	final
	RootServlet rootServlet;
	final
	EmailService emailService;
	
	@Override
	public String getUnfrozenUser(String email, String newPassword) throws SessionExceptions {
		Users itselfMxg = userMxgServlet.getUSerMsgByEmail(email);
		UserStatus status = UserStatus.testEnumValue(itselfMxg.getStatus());
		if (!UserStatus.FROZEN.equals(status))
			throw new OperationException("解除冻结失败!因为用户当前状态为:" + status.getName());
		UnfrozenUser unfrozenUser = new UnfrozenUser();
		unfrozenUser.setUserId(itselfMxg.getUserId());
		unfrozenUser.setNewPassword(passwordEncryptionUtil.hashPassword(newPassword));
		unfrozenUser.setIp(null);
		unfrozenUser.setEmail(itselfMxg.getMailbox());
		String token = tokenService.insertToken(unfrozenUser, TokenType.UNFROZEN_USER, Constant.VERIFY_EMAIL_VALID_TIME).getToken();
		String content =
				"您好，您正在解冻您在 '因果报应的破小站' 的账号。请点击下方按钮复制您的验证令牌到剪贴板,然后贴到令牌确认输入框中.此令牌的有效期时间为 %d 小时。".formatted(
						Constant.VERIFY_EMAIL_VALID_TIME / 1000 / 60 / 60);
		String emailText = LongTextConstant.getEmailCopyText("解冻您的账号", content, emailService.getFROM_EMAIL(), token);
		emailService.sendEmail(itselfMxg.getMailbox(), emailText, "解冻您的账号", true);
		MyLogUtil.UserBehaviorLog(SecureController.class,
		                          "发送解冻邮件至邮箱" + itselfMxg.getMailbox(), itselfMxg);
		return "我们已向您的邮箱 %s 发送了一封验证邮件，有效期为 %d 小时。\n如果未收到邮件，请检查垃圾邮件或稍后重试。".formatted(
				itselfMxg.getMailbox(), Constant.VERIFY_EMAIL_VALID_TIME / 1000 / 60 / 60
		);
	}
	
	@Override
	public String getFrozenUser(String email) throws SessionExceptions {
		Users u = userMxgServlet.getUSerMsgByEmail(email);
		UserStatus status = UserStatus.testEnumValue(u.getStatus());
		if (!(UserStatus.OK.equals(status)
		      || UserStatus.NOT_AUTHENTICATED.equals(status)))
			throw new OperationException("冻结失败!因为用户当前状态为:" + status.getName());
		FrozenUser frozenUser = new FrozenUser();
		frozenUser.setUserId(u.getUserId());
		frozenUser.setIp(null);
		frozenUser.setEmail(u.getMailbox());
		String token = tokenService.insertToken(frozenUser, TokenType.FROZEN_USER, Constant.VERIFY_EMAIL_VALID_TIME).getToken();
		String content =
				"您好，您正在冻结您在 '因果报应的破小站' 的账号。请点击下方按钮复制您的验证令牌到剪贴板,然后贴到令牌确认输入框中.此令牌的有效期时间为 %d 小时。".formatted(
						Constant.VERIFY_EMAIL_VALID_TIME / 1000 / 60 / 60);
		String emailText = LongTextConstant.getEmailCopyText("冻结您的账号", content, emailService.getFROM_EMAIL(), token);
		emailService.sendEmail(u.getMailbox(), emailText, "冻结您的账号", true);
		MyLogUtil.UserBehaviorLog(SecureController.class,
		                          "发送冻结验证邮件至邮箱" + u.getMailbox(), u);
		return "我们已向您的邮箱 %s 发送了一封验证邮件，有效期为 %d 小时。\n如果未收到邮件，请检查垃圾邮件或稍后重试。".formatted(
				u.getMailbox(), Constant.VERIFY_EMAIL_VALID_TIME / 1000 / 60 / 60
		);
	}
	
	@Override
	public String frozenUser(int userId, String token) throws SessionExceptions {
		//检查是否为管理调用
		boolean isRoot = userId != -1;
		//如果是管理员调用检查令牌
		if (isRoot) {
			OnlineUser onlineUser = tokenService.VerifyAndParse(token, -1, TokenType.DEFAULT_STATUS, null);
			UsersWithBLOBs usersWithBLOBs = usersMapper.selectByPrimaryKey(userId);
			if (usersWithBLOBs == null || usersWithBLOBs.getUserId() == null)
				throw new OperationException("被操作的用户不存在");
			if (!(UserStatus.OK.getValue().equals(usersWithBLOBs.getStatus()) ||
			      UserStatus.NOT_AUTHENTICATED.getValue().equals(usersWithBLOBs.getStatus())))
				throw new OperationException("操作失败,因为用户状态为:" + usersWithBLOBs.getStatus());
			Users users = new Users();
			users.setUserId(userId);
			users.setStatus(UserStatus.FROZEN.getValue());
			WriteError.tryWrite(usersMapper.updateByPrimaryKey(users));
			MyLogUtil.info(getClass(), "ID为{%d}的管理员手动冻了ID:%d的用户,该用户现在状态为不可用!".formatted(onlineUser.getUserId(), userId));
			return "操作成功!";
		}
		//验证令牌
		FrozenUser onlineUser = tokenService.VerifyAndParse(token, userId, TokenType.FROZEN_USER, null);
		UsersWithBLOBs users = usersMapper.selectByPrimaryKey(onlineUser.getUserId());
		if (users == null || users.getUserId() == null) {
			passwordEncryptionUtil.applyRandomDelay(0, 30);
			throw new OperationException("用户不存在!");
		}
		UserStatus userStatus = UserStatus.testEnumValue(users.getStatus());
		if (userStatus == UserStatus.OK || UserStatus.NOT_AUTHENTICATED == userStatus) {
			//root 防护
			if (users.getUserId() == 0 || users.getUserId() == 1) {
				//假装成功
				MyLogUtil.info(getClass(), "警告有人尝试冻结特殊账号!");
				return "您已经成功冻结了您的账号,为了您的账号安全考虑,请尽快联系管理员进行处理!";
			}
			//进入冻结
			UsersWithBLOBs usersWithBLOBs = new UsersWithBLOBs();
			usersWithBLOBs.setUserId(users.getUserId());
			usersWithBLOBs.setStatus(UserStatus.FROZEN.getValue());
			WriteError.tryWrite(usersMapper.updateByPrimaryKeySelective(usersWithBLOBs));
			return "您已经成功冻结了您的账号,为了您的账号安全考虑,请尽快联系管理员进行处理!";
		}
		throw new OperationException("冻结失败!因为用户当前状态为:" + userStatus.getName());
	}
	
	@Override
	public String unfrozenUser(int userId, String token, String ip) throws SessionExceptions {
		//检查是否为管理调用
		boolean isRoot = userId != -1;
		//如果是管理员调用检查令牌
		if (isRoot) {
			OnlineUser onlineUser;
			try {
				onlineUser = tokenService.VerifyAndParse(token, -1, TokenType.DEFAULT_STATUS, null);
			} catch (Exception e) {
				passwordEncryptionUtil.applyRandomDelay(0, 30);
				throw e;
			}
			if (!rootServlet.isIPWhiteList(ip)) {
				throw new OperationException("操作失败!因为ip被防火墙隔离了!");
			}
			UsersWithBLOBs usersWithBLOBs = usersMapper.selectByPrimaryKey(userId);
			if (usersWithBLOBs == null || usersWithBLOBs.getUserId() == null)
				throw new OperationException("被操作的用户不存在");
			//无视状态件直接解冻
			Users users = new Users();
			users.setUserId(userId);
			users.setStatus(UserStatus.OK.getValue());
			WriteError.tryWrite(usersMapper.updateByPrimaryKey(users));
			MyLogUtil.info(getClass(), "ID为{%d}的管理员手动解冻了ID:%d的用户,该用户现在状态为正常!".formatted(onlineUser.getUserId(), userId));
			return "操作成功!";
		}
		//用户自助操作
		UsersWithBLOBs user = userMxgServlet.getItselfMxg(userId);
		UserStatus userStatus = UserStatus.testEnumValue(user.getStatus());
		if (userStatus == UserStatus.FROZEN) {
			//验证令牌
			UnfrozenUser onlineUser;
			try {
				onlineUser = tokenService.VerifyAndParse(token, -1, TokenType.UNFROZEN_USER, null);
			} catch (Exception e) {
				passwordEncryptionUtil.applyRandomDelay(0, 30);
				throw e;
			}
			if (onlineUser instanceof UnfrozenUser u) {
				if (!u.getEmail().equals(user.getMailbox()) || !(u.getUserId() == user.getUserId())) {
					throw new OperationException("解冻失败,因为令牌提供的用户信息和实际的不一致");
				}
				//解冻,并重置密码
				UsersWithBLOBs u1 = new UsersWithBLOBs();
				u1.setUserId(u.getUserId());
				u1.setStatus(UserStatus.OK.getValue());
				u1.setUserPassword(u.getNewPassword());
				WriteError.tryWrite(usersMapper.updateByPrimaryKeySelective(u1));
				return "解冻成功";
			}
			throw new OperationException("错误的令牌类型!");
		}
		throw new OperationException("解冻失败!因为该用户当前状态为:" + userStatus.getName());
	}
	
	@Override
	public Feedback feedback(Feedback feedback) {
		WriteError.tryWrite(feedbackMapper.insert(feedback));
		return feedback;
	}
	
	@Override
	public String forgetPassword(String email, String ip) throws UserException, SessionExceptions {
		UsersExample example = new UsersExample();
		example.createCriteria().andMailboxEqualTo(email);
		List<Users> users = usersMapper.selectByExample(example);
		if (users.isEmpty() || users.getFirst().getUserId() == null) {
			throw new OperationException("用户不存在!");
		}
		if (users.size() == 1) {
			Users user1 = users.getFirst();
			Session session = sessionService.getSession(user1.getUserId());
			if (session != null && session.getSessionId() != null) {
				//不在线或者为空时可以继续
				if (Boolean.TRUE.equals(session.getStatus())) {
					MyLogUtil.info(SessionServiceIMPL.class, "用户%d:{%s}{邮箱:%s}当前在线,无法进行密码重置!\nip地址为:{%s}".formatted(
							user1.getUserId(), user1.getUserName(), user1.getMailbox(), ip
					));
					throw new OperationException("您的用户当前在线,无法进行密码重置!如果这不是您,请立即冻结账号并联系管理员");
				}
			}
			//生成验证令牌
			LostPasswordUser user = new LostPasswordUser();
			user.setEmail(email);
			user.setIp(ip);
			user.setTokenType(TokenType.LOST_PASSWORD);
			user.setUserId(users.getFirst().getUserId());
			UserToken userToken = tokenService.insertToken(user, TokenType.LOST_PASSWORD, Constant.VERIFY_EMAIL_VALID_TIME);
			return userToken.getToken();
		}
		throw new UserException("用户重复!", users, ErrorEnum.USER_REPEAT_ERROR);
	}
	
	@Override
	public String changePassword(String token, String ip, String newPassword) throws BestException {
		LostPasswordUser u = tokenService.VerifyAndParse(token, -1, TokenType.LOST_PASSWORD, ip);
		//root 防护
		if (u.getUserId() == 0 || u.getUserId() == 1) {
			return "密码已更新!请重新登录";//假装修改成功
		}
		UsersWithBLOBs usersWithBLOBs = new UsersWithBLOBs();
		usersWithBLOBs.setUserId(u.getUserId());
		usersWithBLOBs.setStatus(UserStatus.OK.getValue());
		usersWithBLOBs.setUserPassword(passwordEncryptionUtil.hashPassword(newPassword));
		newPassword = null;
		WriteError.tryWrite((usersMapper.updateByPrimaryKeySelective(usersWithBLOBs)));
		Session session = sessionService.getSession(u.getUserId());
		if (session != null && session.getSessionId() != null) {sessionService.exitLogin(u.getUserId(), true, null);}
		return "密码已更新!请重新登录";
	}
	
	@Override
	public String authenticationEmail(String ip, int userId, String token) throws SessionExceptions {
		//验证令牌
		VerifyEmail onlineUser = tokenService.VerifyAndParse(token, userId, TokenType.VERIFY_EMAIL, ip);
		UsersWithBLOBs itselfMxg = usersMapper.selectByPrimaryKey(userId);
		if (itselfMxg.getJurisdiction() != 2) {
			return "您已经认证过了,不需要再次认证";
		}
		//验证成功,将用户状态设置为正常
		UsersWithBLOBs users = new UsersWithBLOBs();
		users.setUserId(onlineUser.getUserId());
		users.setMailbox(onlineUser.getEmail());
		users.setJurisdiction(JurisdictionLevel.USERS_JURISDICTION.getLevel());
		users.setStatus(UserStatus.OK.getValue());
		WriteError.tryWrite(usersMapper.updateByPrimaryKeySelective(users));
		return "您已成功完成邮箱认证!如果主页面仍然显示未认证,请刷新页面或者清除本地缓存";
	}
}
