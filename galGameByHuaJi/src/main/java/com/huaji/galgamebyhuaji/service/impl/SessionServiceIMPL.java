package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.SessionMapper;
import com.huaji.galgamebyhuaji.entity.Session;
import com.huaji.galgamebyhuaji.entity.SessionExample;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.SessionService;
import com.huaji.galgamebyhuaji.service.TokenService;
import io.micrometer.common.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Transactional
public class SessionServiceIMPL implements SessionService {
	@Autowired
	SessionMapper sessionMapper;
	@Autowired
	TokenService tokenService;
	
	//由于此服务类是仅由用户服务类调用,因此不需要考虑线程安全
	@Override
	public UserToken UpdateUserLoginTime (Integer user, long time, String loginIP) throws SessionExceptions {
		Session session = getSession(user, loginIP);
		boolean isFirstLogin = false;
		if ( session == null ) {
			//不存在会话时新建
			session = new Session();
			isFirstLogin = true;
			session.setUserId(user);
		}
		if ( !isFirstLogin ) {//非第一次登录时检查之前令牌状态
			if ( !session.getStatus() ) {//ture为当前在线
				if ( !MyStringUtil.isNull(session.getLastLoginIp()) && !session.getLastLoginIp().equals(loginIP) )//上次登录地点不为空,并且不匹配
					throw new SessionExceptions("错误!您的账号已经在其他地点登陆了登录ip为:%s".formatted(session.getLastLoginIp()), ErrorEnum.SESSION_DIFFERENT_ERROR);
			}
		}
		OnlineUser onlineUser = new OnlineUser();
		onlineUser.setUserId(user);
		onlineUser.setIp(loginIP);
		onlineUser.setTokenType(TokenType.DEFAULT_STATUS);
		UserToken userToken = tokenService.insertToken(onlineUser, TokenType.DEFAULT_STATUS, time);
		session.setLastLoginIp(loginIP);
		session.setLastLoginTime(new Date());
		session.setStatus(true);
		session.setTokenId(userToken.getTokenId());
		if ( isFirstLogin )
			WriteError.tryWrite(sessionMapper.insertSelective(session));
		else
			WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(session));
		return userToken;
	}
	
	@Override
	public Session testLoginTime (Integer usersId) throws SessionExceptions {
		Session session = getSession(usersId);
		if ( session == null || session.getSessionId() == null )
			throw new OperationException("登录信息验证失败!请重新登录!");
		//验证用户状态和是否在线
		if ( !session.getStatus() ) throw new OperationException("您的登录已过期,请重新登录!");
		return session;
	}
	
	@Override
	public Session getSession (Integer userId) throws SessionExceptions {
		return getSession(userId, null);
	}
	
	@Nullable
	private Session getSession (Integer user, String loginIP) throws SessionExceptions {
		if ( MyStringUtil.isNull(loginIP) )
			loginIP = null;
		if ( user == null )
			throw new SessionExceptions("用户不存在", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
		List<Session> sessionList = sessionMapper.getSessionByUser(user, loginIP);
		if ( sessionList == null || sessionList.isEmpty() ) {
			//不存在会话时返回空
			return null;
		} else if ( sessionList.size() == 1 ) {
			//存在一个会话时
			return sessionList.getFirst();
		}
		throw new SessionExceptions("错误!存在多个会话信息,请联系管理员进行删除", ErrorEnum.SESSION_REPEAT_ERROR);
	}
	
	@Override
	public int manbaOut () {
		//仅建议在服务器关闭时调用一次,因为该方法会将所有用户设置为离线状态
		return sessionMapper.helicopterAirCrash();
	}
	
	@Override
	public String exitLogin (Integer usersId, boolean isAutomaticOut, String token) throws SessionExceptions {
		if ( usersId == null ) throw new OperationException("用户不存在");
		ReentrantLock lockForUser = GlobalLock.getLockForUser(usersId);
		try {
			lockForUser.lock();
			if ( isAutomaticOut ) {
				WriteError.tryWrite(sessionMapper.exitLogin(usersId));
				return "已经成功退出";
			}
			Session session = getSession(usersId);
			if ( session == null || session.getSessionId() == null )
				throw new SessionExceptions("错误!您的会话信息不存在,无法退出登录!", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
			SessionExample sessionExample = new SessionExample();
			if ( !session.getStatus() )
				throw new SessionExceptions("错误!您已经退出登录了,无法再次退出登录!", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
			session.setStatus(false);
			tokenService.invalidateToken(token, usersId);
			sessionExample.createCriteria().andSessionIdEqualTo(session.getSessionId());
			WriteError.tryWrite(sessionMapper.updateByExampleSelective(session, sessionExample));
			return "您已经成功退出登录,我们期待您的再次访问和使用";
		} finally {
			GlobalLock.unlockForUser(lockForUser, usersId);
		}
	}
}
//	@Autowired
//	SessionMapper sessionMapper;
//	@Autowired
//	UsersMapper usersMapper;
//
//	@Override
//	public ReturnResult<Session> UpdateUserLoginTime(Users user, long time, String loginIP) throws SessionExceptions {
//		Integer userId = user.getUserId();
//		if (userId == null) throw new OperationException("用户不存在");
//		Session session = getUserSession(user.getUserId());
//		Date nowTime = new Date();
//		synchronized (userId) {
//			if (session != null && session.getSessionId() != null) {//在会话表里有
//				if (LoginStatus.NOT_AVAILABLE.getValue().equals(session.getStatus()))
//					throw new OperationException("登陆失败,因为您的会话状态错误!");
//				if (LoginStatus.OFFLINE.getValue().equals(session.getStatus())) {//正常时
//					//尝试进行签到,仅有此情况是需要进行签到的
//					session.setLastLoginIp(loginIP);
//					session.setLastLoginTime(nowTime);
//					session.setSessionDieTime(new Date(System.currentTimeMillis() + time));
//					session.setStatus(LoginStatus.ONLINE.getValue());
//					WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(session));
//					return ReturnResult.isTrue("登录成功!%s".formatted(ClockIn(userId)), session);
//				} else {
//					if (MyStringUtil.isNull(loginIP) || !loginIP.equals(session.getLastLoginIp()))//不正常
//						//没有获取到登陆地点或者没匹配上:
//						throw new SessionExceptions(
//								"错误!您的账号已经在其他地点登陆了当前IP为" + loginIP +
//										",上次登陆IP:" + session.getLastLoginIp() + "如果不是您本人操作请尽快冻结账号并联系管理员,以免造成误会"
//								, ErrorEnum.SESSION_DIFFERENT_ERROR);
//					session.setSessionDieTime(new Date(session.getLastLoginTime().getTime() + TimeUnit.HOURS.toMillis(1)));//对于已经登录的延长1小时
//					WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(session));
//					return ReturnResult.isTrue("登录成功!", session);
//				}
//			} else {//没有时
//				session = new Session();
//				session.setLastLoginIp(loginIP);
//				session.setLastLoginTime(nowTime);
//				session.setSessionDieTime(new Date(System.currentTimeMillis() + time));
//				session.setUserId(user.getUserId());
//				session.setStatus(LoginStatus.ONLINE.getValue());
//				WriteError.tryWrite(sessionMapper.insertSelective(session));
//				return ReturnResult.isTrue("登录成功!%s".formatted(ClockIn(userId)), session);
//			}
//		}
//	}

//
//	@Nullable
//	private Session getUserSession(Integer usersId) throws SessionExceptions {
//		if (usersId == null)
//			throw new OperationException("用户不存在");
//		SessionExample sessionExample = new SessionExample();
//		sessionExample.createCriteria().andUserIdEqualTo(usersId);
//		List<Session> sessionsList = sessionMapper.selectByExample(sessionExample);
//		if (sessionsList == null || sessionsList.isEmpty())
//			return null;
//		Session session = sessionsList.getFirst();
//		if (sessionsList.size() > 1)//非致命错误记录即可
//			MyLogUtil.error(SessionServiceIMPL.class, new SessionExceptions("存在多个会话信息", sessionsList, ErrorEnum.SESSION_REPEAT_ERROR));
//		if (SessionStatus.NOT_AVAILABLE.getValue().equals(session.getStatus()))
//			throw new SessionExceptions("错误,您的账户状态异常", ErrorEnum.SESSION_REPEAT_ERROR);
//		return session;
//	}
//
//	@Override
//	public Session testLoginTime(Integer usersId) throws SessionExceptions {
//		Session session = getUserSession(usersId);
//		if (session == null) throw new OperationException("您的会话已经过期了,请重新前往登录页面登录!");
//		else if (MyStringUtil.isNull(session.getStatus()))
//			throw new OperationException("您的会话已经过期了,请重新前往登录页面登录!");
//		if (LoginStatus.ONLINE.getValue().equals(session.getStatus())) {
//			Date nowTime = new Date();
//			if (nowTime.after(session.getSessionDieTime()))
//				throw new OperationException("您的会话已经过期了,请重新前往登录页面登录!");
//		}
//		return session;
//	}
//
//	@Override
//	public int manbaOut() {
//		return sessionMapper.helicopterAirCrash();
//	}
//
//	@Override
//	public String exitLogin(Integer usersId, boolean isAutomaticOut) throws SessionExceptions {
//		synchronized (usersId) {
//			Session session = getUserSession(usersId);
//			if (session == null || session.getSessionId() == null)
//				throw new SessionExceptions("错误!您的会话信息不存在,无法退出登录!", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
//			if (SessionStatus.OFFLINE.getValue().equals(session.getStatus()))
//				throw new OperationException("退出失败,因为您已经退出了");
//			session.setStatus(SessionStatus.OFFLINE.getValue());
//			WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(session));
//			//完成会话表更新
//			return "退出登录成功!";
//		}
//	}
//}