package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
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
import com.huaji.galgamebyhuaji.model.jwtToken.LostPasswordUser;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.model.jwtToken.VerifyEmail;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.SecureServlet;
import com.huaji.galgamebyhuaji.service.SessionService;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
	
	@Override
	public String frozenUser(Users user) {
		UsersWithBLOBs itselfMxg = userMxgServlet.getItselfMxg(user.getUserId());
		if (itselfMxg == null || itselfMxg.getUserId() == null) throw new OperationException("用户不存在!");
		UserStatus userStatus = UserStatus.testEnumValue(itselfMxg.getStatus());
		if (userStatus == UserStatus.OK || UserStatus.NOT_AUTHENTICATED == userStatus) {
			//root 防护
			if (user.getUserId() == 0 || user.getUserId() == 1) {
				return "冻结成功";//假装成功
			}
			//进入冻结
			UsersWithBLOBs usersWithBLOBs = new UsersWithBLOBs();
			usersWithBLOBs.setUserId(itselfMxg.getUserId());
			usersWithBLOBs.setStatus(UserStatus.FROZEN.getValue());
			WriteError.tryWrite(usersMapper.updateByPrimaryKeySelective(usersWithBLOBs));
			return "冻结成功";
		}
		throw new OperationException("冻结失败!因为该用户当前状态为:" + userStatus.getName());
	}
	
	@Override
	public String unfrozenUser(int userId, String token, String ip) throws SessionExceptions {
		UsersWithBLOBs user = userMxgServlet.getItselfMxg(userId);
		UserStatus userStatus = UserStatus.testEnumValue(user.getStatus());
		if (userStatus == UserStatus.FROZEN) {
			//验证令牌
			OnlineUser onlineUser = tokenService.VerifyAndParse(token, userId, TokenType.LOST_PASSWORD, ip);
			if (onlineUser instanceof LostPasswordUser u) {
				if (!(u.getEmail().equals(user.getMailbox()) || user.getUserId().equals(u.getUserId()))) {
					throw new OperationException("解冻失败,因为令牌提供的用户信息和实际的不一致");
				}
				//解冻
				UsersWithBLOBs u1 = new UsersWithBLOBs();
				u1.setUserId(u.getUserId());
				u1.setStatus(UserStatus.OK.getValue());
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
					throw new OperationException("您的用户当前在线,无法进行密码重置!如果这不是您,请直接冻结账号并联系管理员");
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
		UsersWithBLOBs itselfMxg = userMxgServlet.getItselfMxg(userId);
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
