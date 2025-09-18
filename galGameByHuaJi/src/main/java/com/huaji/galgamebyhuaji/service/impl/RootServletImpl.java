package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.SessionMapper;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.Session;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.enumPackage.UserStatus;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.RootServlet;
import com.huaji.galgamebyhuaji.service.SessionService;
import com.huaji.galgamebyhuaji.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RootServletImpl implements RootServlet {
	@Autowired
	UsersMapper usersMapper;
	@Autowired
	TokenService tokenService;
	@Autowired
	PasswordEncryptionUtil passwordEncryptionUtil;
	@Autowired
	SessionMapper sessionMapper;
	@Autowired
	SessionService sessionService;
	
	@Override
	public Users RootEditUserHeadPortrait(int usersId, int rootId, MultipartFile jpeg) throws WriteError {
		return null;
	}
	
	@Override
	public Users RootEditUserMxg(UsersWithBLOBs users, int rootId) throws WriteError {
		return null;
	}
	
	@Override
	public Users RootUpdateUSerStatus(int usersId, int rootId, UserStatus userStatus) throws WriteError {
		return null;
	}
	
	@Override
	public Users RootSelectUserById(int usersId, int rootId) {
		return usersMapper.selectByPrimaryKey(usersId);
	}
	
	@Override
	public List<Users> RootSelectUserByName(String usersName, int root) {
		return List.of();
	}
	
	private static final String[] IP_WHITELIST = new String[]{
			// IPv4
			"127.0.0.1",
			"localhost",
			// 其他 IPv4
			"127.0.0.2",
			"127.0.1.1",
			// IPv6
			"::1",
			"0:0:0:0:0:0:0:1"
	};
	
	
	@Override
	@Transactional
	public UserToken rootLogin(String token, HttpServletRequest request) {
		ReentrantLock lock = null;
		UserToken userToken;
		String ip = ElseUtil.getClientIp(request);
		try {
			MyLogUtil.info(LoginService.class, "ip:{%s}正在登录特殊root用户".formatted(ip));
			if (!MyStringUtil.isNull(token) && token.length() >= 20) {
				lock = GlobalLock.getLockForUser(token.hashCode());
				lock.lock();
				userToken = tokenService.verifyToken(token, -1, ip, true);
				//手动校验ip
				boolean b = false;
				for (String s : IP_WHITELIST) {
					if (s.equals(ip)) {
						b = true;
						break;
					}
				}
				if (!b) {
					MyLogUtil.info(LoginService.class, "ip:{%s}正在登录特殊root用户时失败了,因为ip不在白名单之内".formatted(ip));
					throw new OperationException("您使用的ip被防火墙隔离了,请换个ip试试");
				}
				return userToken;
			}
			else {
				MyLogUtil.error(LoginService.class,
				                "ip:{%s}登录特殊root用户时失败了,因为输入长度不达标".formatted(ip));
			}
		} catch (Exception e) {
			MyLogUtil.error(LoginService.class,
			                "ip:{%s}正在登录特殊root用户时失败了,因为:".formatted(ip) + e.getMessage());
		} finally {
			if (lock != null) {GlobalLock.unlockForUser(lock, token.hashCode());}
		}
		passwordEncryptionUtil.applyRandomDelay(10, 500);
		throw new OperationException("账号或密码不正确，请检查后重试。");
	}
	
	
	@Override
	public void rootUserInit() throws SessionExceptions {
		//设置token
		OnlineUser onlineUser0 = new OnlineUser();
		onlineUser0.setIp(null);
		onlineUser0.setUserId(0);
		onlineUser0.setTokenType(TokenType.DEFAULT_STATUS);
		OnlineUser onlineUser1 = new OnlineUser();
		onlineUser1.setIp(null);
		onlineUser1.setUserId(1);
		onlineUser1.setTokenType(TokenType.DEFAULT_STATUS);
		UserToken userToken0 = tokenService.insertToken(onlineUser0, TokenType.DEFAULT_STATUS,
		                                                1000L * 60 * 60 * 24 * 100);
		UserToken userToken1 = tokenService.insertToken(onlineUser1, TokenType.DEFAULT_STATUS,
		                                                1000L * 60 * 60 * 24 * 100);
		MyLogUtil.info(LoginService.class, "***********************************************************");
		MyLogUtil.info(LoginService.class, "***********************root登录令牌更新**********************");
		MyLogUtil.info(LoginService.class, "***********************************************************");
		MyLogUtil.info(LoginService.class, "0号root用户登录令牌:" + userToken0.getToken());
		System.out.println("0号root用户登录令牌:" + userToken0.getToken());
		MyLogUtil.info(LoginService.class, "1号root用户登录令牌:" + userToken1.getToken());
		MyLogUtil.info(LoginService.class, "***********************************************************");
		MyLogUtil.info(LoginService.class, "***********此令牌仅本次服务器启动时有效,服务器关闭后失效************");
		MyLogUtil.info(LoginService.class, "***********************************************************");
		System.out.println("1号root用户登录令牌:" + userToken1.getToken());
		//将两个root设置为在线
		Session user0Session = sessionService.getSession(0);
		boolean hasUser0 = user0Session != null;
		Session user1Session = sessionService.getSession(1);
		boolean hasUser1 = user1Session != null;
		if (!hasUser0) {
			user0Session = new Session();
			user0Session.setUserId(0);
			user0Session.setLastLoginTime(new Date());
		}
		if (!hasUser1) {
			user1Session = new Session();
			user1Session.setUserId(1);
			user1Session.setLastLoginTime(new Date());
		}
		user0Session.setLastLoginIp("null");//置为空,因为手动接管了ip校验
		user0Session.setStatus(true);//设置为在线
		user1Session.setLastLoginIp("null");
		user1Session.setStatus(true);//设置为在线
		//更新/插入
		if (hasUser0)
			WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(user0Session));
		else
			WriteError.tryWrite(sessionMapper.insert(user0Session));
		if (hasUser1)
			WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(user1Session));
		else
			WriteError.tryWrite(sessionMapper.insert(user1Session));
	}
}
