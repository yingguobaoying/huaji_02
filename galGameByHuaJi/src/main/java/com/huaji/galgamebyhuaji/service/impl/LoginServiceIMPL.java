package com.huaji.galgamebyhuaji.service.impl;


import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.Session;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import com.huaji.galgamebyhuaji.enumPackage.UserStatus;
import com.huaji.galgamebyhuaji.exceptions.*;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import static com.huaji.galgamebyhuaji.constant.Constant.CHECK_IN_SCORE;

@Service
@Transactional
public class LoginServiceIMPL implements LoginService {
	private final UsersMapper usersMapper;
	final
	PasswordEncryptionUtil passwordEncryptionUtil;
	final
	SessionService sessionService;
	final
	RedisMemoryService redisMemoryService;
	final
	TagService tagService;
	final
	TokenService tokenService;
	private static final String EMAIL_REGEX =
			"^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*" +
					"@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
	private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
	
	public LoginServiceIMPL(UsersMapper usersMapper, PasswordEncryptionUtil passwordEncryptionUtil, SessionService sessionService, RedisMemoryService redisMemoryService, TagService tagService, TokenService tokenService) {
		this.usersMapper = usersMapper;
		this.passwordEncryptionUtil = passwordEncryptionUtil;
		this.sessionService = sessionService;
		this.redisMemoryService = redisMemoryService;
		this.tagService = tagService;
		this.tokenService = tokenService;
	}
	
	
	@Override
	public UserToken login(UsersWithBLOBs users, HttpServletRequest request, Long KeepTime) throws BestException {
		List<UsersWithBLOBs> login = usersMapper.login(users);
		//理论上应该只会匹配到一个用户
		if (login == null || login.isEmpty()) {
			//防时序攻击,匹配用户名
			passwordEncryptionUtil.verifyPassword("1145141919810", Constant.CONSTANT_PASSWORD);
			throw new OperationException("账号或密码不正确，请检查后重试。");
		}
		if (login.size() != 1)
			throw new UserException("系统检测到账户信息存在异常，为确保您的账户安全，请稍后重试或联系站长。", login, ErrorEnum.USER_REPEAT_ERROR);
		UsersWithBLOBs loginUser = login.getFirst();
		ReentrantLock lock = GlobalLock.getLockForUser(loginUser.getUserId());
		boolean b = passwordEncryptionUtil.verifyPassword(
				users.getUserPassword()
				, loginUser.getUserPassword()
		);
		if (!b) throw new OperationException("账号或密码不正确，请检查后重试。");
		users.setUserPassword(null);
		//检查用户状态
		UserStatus userStatus = UserStatus.testEnumValue(loginUser.getStatus());
		if (!(userStatus == UserStatus.NOT_AUTHENTICATED || userStatus == UserStatus.OK)) {
			throw new OperationException("您的用户状态错误!当前用户状态:%s".formatted(
					userStatus.getName()
			));
		}
		try {
			lock.lock();
			//签到反馈信息(临时存储)
			request.setAttribute(SystemConstant.SYSTEM_MSG, ClockIn(loginUser.getUserId()));
			//更新/增加会话信息
			return sessionService.UpdateUserLoginTime(loginUser.getUserId(), KeepTime, ElseUtil.getClientIp(request));
		} finally {
			GlobalLock.unlockForUser(lock, loginUser.getUserId());
		}
		
	}
	
	@Override
	public ReturnResult<Users> register(UsersWithBLOBs users) {
		String s = users.getMailbox() + users.getUserPe();
		int userHas = s.hashCode();
		if (userHas > 0) userHas = -userHas;//确保为负数,避免占用正数对象
		ReentrantLock lockForUserName = GlobalLock.getLockForUser(userHas);
		try {
			lockForUserName.lock();
			ReturnResult<Users> usersReturnResult = testRegisterMxg(users, null);
			//重复检查
			if (!usersReturnResult.isOperationResult()) return usersReturnResult;
			users.setUserId(null);//自动递增主键置空,等待数据库填充
			users.setUserPassword(passwordEncryptionUtil.hashPassword(users.getUserPassword()));
			if (MyStringUtil.isNull(users.getBio()))
				users.setBio("ta还没有填写简介信息");
			users.setRegisterTime(new Date());
			users.setUserHeadPortraitUrl(Constant.DEFAULT_HEAD_PORTRAIT);
			users.setJurisdiction(JurisdictionLevel.NOT_VALIDATED.getLevel());
			users.setCoin(Constant.INIT_COIN);
			if (MyStringUtil.isNull(users.getSex()))
				users.setSex("不愿透露");
			if (users.getBirthday() == null)
				users.setBirthday(new Date(0));
			WriteError.tryWrite(usersMapper.insertSelective(users));
			if (users.getUserId() == null)
				throw new OperationException("注册失败,请重试");
			Users u = new Users();
			u.setUserId(users.getUserId());
			u.setUserHeadPortraitUrl(users.getUserHeadPortraitUrl());
			u.setUserName(users.getUserName());
			redisMemoryService.saveData(u);
			return ReturnResult.isTrue("注册成功!您现在可以返回登录了。为了解锁全部功能、保障账户安全，请务必前往【个人中心】完成邮箱验证。", users);
		} finally {
			GlobalLock.unlockForUser(lockForUserName, userHas);
		}
	}
	
	@Override
	public Users testLogin(String usersToken) throws UserException, SessionExceptions {
		return testLogin(usersToken, JurisdictionLevel.NOT_VALIDATED.getLevel());
	}
	
	@Override
	public Users testLogin(String usersToken, int jurisdiction) throws SessionExceptions {
		JurisdictionLevel needJurisdiction = JurisdictionLevel.getJurisdiction(jurisdiction);
		UserToken userToken = tokenService.verifyToken(usersToken, -1, null, true);
		//验证当前session状态
		sessionService.testLoginTime(userToken.getUserId());
		//获取用户信息
		Users userJurisdictionMsg = usersMapper.getUserJurisdiction(userToken.getUserId());
		if (userJurisdictionMsg == null || userJurisdictionMsg.getUserId() == null)
			throw new OperationException("错误!不存在用户ID为:%s的用户!".formatted(userToken.getUserId()));
		//检查权限是否足够
		Integer jurisdictionLever = userJurisdictionMsg.getJurisdiction();
		if ( //但用户的权限为为认证,并且需要的权限恰好为正常用户时触发特殊提醒
				jurisdictionLever == JurisdictionLevel.NOT_VALIDATED.getLevel()
						&& needJurisdiction.getLevel() == JurisdictionLevel.USERS_JURISDICTION.getLevel()) {
			throw new OperationException("此项操作需要您先完成邮箱验证。请前往【个人中心】完善验证，以便获得完整的使用体验。");
		}
		if (needJurisdiction.getLevel() > jurisdictionLever)
			throw new OperationException(
					"抱歉，您当前的权限（{%s}）无法执行此操作。该功能需要 {%s} 及以上权限才能访问"
							.formatted(
									needJurisdiction.getName(),
									JurisdictionLevel.getJurisdiction(jurisdictionLever).getName())
			);
		return userJurisdictionMsg;
	}
	
	@Override
	public ReturnResult<Users> testRegisterMxg(Users users, Integer testUserId) {
		if (MyStringUtil.isNull(users.getUserPe()))
			users.setUserPe(null);//防止出现,非必要的登录字段为空
		if (MyStringUtil.isNull(users.getMailbox())) throw new OperationException("邮箱不可为空，请填写邮箱地址。");
		if (MyStringUtil.isNull(users.getUserNameLogin()))
			throw new OperationException("用户名不可为空，请设置您的昵称或用户名。");
		if (!emailPattern.matcher(users.getMailbox()).matches()) {
			throw new OperationException("邮箱格式不正确，请检查后重新输入。");
		}
		if (!MyStringUtil.isNull(users.getUserPe()) && !MyStringUtil.isValidChinesePhoneNumber(users.getUserPe())) {
			throw new OperationException("手机号码格式有误，请输入有效的中国大陆手机号。");
		}
		List<Users> users1 = usersMapper.testRegisterMxg(users);
		if (users1 == null || users1.isEmpty())
			return new ReturnResult<Users>().operationTrue("信息验证成功,当前信息无人使用", null);
		boolean isLoginName = true;
		boolean isUserEmail = true;
		boolean isUserPE = true;
		boolean hasPe = !MyStringUtil.isNull(users.getUserPe());
		Users returnUser = new Users();
		if (users1.size() == 1 && testUserId != null) {
			if (testUserId.equals(users1.getFirst().getUserId()))
				return new ReturnResult<Users>().operationTrue("信息验证成功,当前信息无人使用", null);
		}
		for (Users u : users1) {
			if (isLoginName && equalsRepetition(users.getUserNameLogin(), u)) {
				isLoginName = false;
				returnUser.setUserNameLogin(users.getUserNameLogin());
			}
			if (isUserEmail && equalsRepetition(users.getMailbox(), u)) {
				isUserEmail = false;
				returnUser.setMailbox(users.getMailbox());
			}
			if (isUserPE && hasPe && equalsRepetition(users.getUserPe(), u)) {
				isUserPE = false;
				returnUser.setUserPe(users.getUserPe());
			}
			if (!isLoginName && !isUserEmail && !isUserPE) break;
		}
		StringBuilder sb = new StringBuilder("用户注册信息重复：");
		if (!isLoginName) sb.append(" 登录名");
		if (!isUserEmail) sb.append(" 邮箱");
		if (!isUserPE) sb.append(" 手机号");
		ReturnResult<Users> usersReturnResult = new ReturnResult<Users>().operationFalse(sb.toString());
		usersReturnResult.setReturnResult(returnUser);
		return usersReturnResult;
	}
	
	private boolean equalsRepetition(String s, Users u) {
		return s.equals(u.getMailbox()) ||
				s.equals(u.getUserPe()) ||
				s.equals(u.getUserNameLogin());
	}
	
	public String ClockIn(Integer userId) throws SessionExceptions {
		boolean clockInResult = false;
		if (userId == null)
			throw new SessionExceptions("用户信息不存在或已被删除，请重新登录。", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
		Session session = sessionService.getSession(userId);
		ReentrantLock lockForUser = GlobalLock.getLockForUser(userId);
		try {
			lockForUser.lock();
			if (session == null) {//不存在会话时说明是第一次登陆签到成功
				clockInResult = true;
			} else {
				Date lastLoginTime = session.getLastLoginTime();
				clockInResult = TimeUtil.isYesterdayOrEarlier(lastLoginTime);
			}
			if (clockInResult) {//上次登陆时间在昨天或者以前,开始签到
				WriteError.tryWrite(usersMapper.userClockIn(userId, CHECK_IN_SCORE));
				return "签到成功!您获得了%d个硬币(积分)".formatted(CHECK_IN_SCORE);
			}
			return "";
		} finally {
			GlobalLock.unlockForUser(lockForUser, userId);
		}
	}
	
	@Override
	public UserToken loginByToken(String token, HttpServletRequest request) throws BestException {
		UserToken userToken = tokenService.verifyToken(token, -1, ElseUtil.getClientIp(request), true);
		ReentrantLock lock = GlobalLock.getLockForUser(userToken.getUserId());
		try {
			lock.lock();
			//签到反馈信息(临时存储)
			request.setAttribute(SystemConstant.SYSTEM_MSG, ClockIn(userToken.getUserId()));
			//更新/增加会话信息
			return sessionService.UpdateUserLoginTime(userToken.getUserId(), TimeUnit.HOURS.toMillis(1), ElseUtil.getClientIp(request));
		} finally {
			GlobalLock.unlockForUser(lock, userToken.getUserId());
		}
	}
}
