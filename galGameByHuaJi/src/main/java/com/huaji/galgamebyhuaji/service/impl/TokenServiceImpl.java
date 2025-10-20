package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.config.JWTConfig;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.dao.UserTokenMapper;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.myUtil.ElseUtil;
import com.huaji.galgamebyhuaji.myUtil.JWTUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.RedisMemoryService;
import com.huaji.galgamebyhuaji.service.TokenService;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static com.huaji.galgamebyhuaji.constant.GlobalLock.getLockForUser;
import static com.huaji.galgamebyhuaji.constant.GlobalLock.unlockForUser;


@Service
@Transactional
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
	final
	UserTokenMapper userTokenMapper;
	final
	JWTUtil jwtUtil;
	final
	JWTConfig jwtConfig;
	final
	RedisMemoryService redisMemoryService;
	static final int TOKEN_CACHE_USER_TOKEN = 1;
	static final int TOKEN_CACHE_DATA = 2;
	
	/**
	 * 生成缓存键
	 */
	private String buildCacheKey(String token, int cacheType) {
		return String.format("%s:%d:%s",
		                     TOKEN_CACHE_KEY,
		                     cacheType,
		                     // 对token进行安全编码，防止特殊字符问题
		                     URLEncoder.encode(token, StandardCharsets.UTF_8)
		);
	}
	
	private <T> void setTokenCache(String token, T tokenCache, int type) {
		redisMemoryService.saveData(
				buildCacheKey(token, type), tokenCache, jwtUtil.getTokenExpireTime(token));
	}
	
	private <T> void delTokenCache(String token) {
		redisMemoryService.delData(buildCacheKey(token, TOKEN_CACHE_USER_TOKEN));
		redisMemoryService.delData(buildCacheKey(token, TOKEN_CACHE_DATA));
	}
	
	public <T extends OnlineUser> UserToken verifyToken(String token, int userId, TokenType type,
	                                                    @Nullable String ip, boolean needUpdate)
			throws SessionExceptions {
		//检查缓存,缓存有就直接过
		UserToken returnMsg = redisMemoryService.getDataTheValidityTimeGreater(
				buildCacheKey(token, TOKEN_CACHE_USER_TOKEN), 5);
		if (returnMsg != null) {//命中缓存,如果自动续期的时间需要的时间>5分钟
			return returnMsg;
		}
		// 1. 基础令牌验证
		if (!jwtUtil.isTokenUsable(token)) {
			throw new OperationException("令牌已过期");
		}
		// 2. 解析JWT内容
		Class<T> tClass = (Class<T>) type.getTokenClazz();
		ReturnResult<T> result = jwtUtil.parseToken(token, SystemConstant.JWT_TOKEN_NAME, tClass);
		if (userId == -1)
			userId = result.getReturnResult().getUserId();
		if (result.isHasError()) {
			throw new OperationException("令牌解析失败: " + result.getMsg());
		}
		T onlineUser = result.getReturnResult();
		if (-1 != userId && !TokenType.DEFAULT_STATUS.equals(type))
			// 3. 关键信息校验
			validateTokenConsistency(userId, type, ip, onlineUser);
		ReentrantLock lock = getLockForUser(onlineUser.getUserId());
		try {
			lock.lock();
			// 4. 数据库令牌验证
			List<UserToken> validTokens = userTokenMapper.verifyToken(token, userId, type.getStatusNum());
			validateTokenCount(validTokens);
			UserToken userToken = validTokens.getFirst();
			// 5. 安全延期令牌
			if (needUpdate)
				return maybeRefreshToken(token, userToken);
			else {
				//扔到缓存里
				setTokenCache(token, userToken, TOKEN_CACHE_USER_TOKEN);
				return userToken;
			}
		} finally {
			unlockForUser(lock, onlineUser.getUserId());
		}
	}
	
	@Override
	public <T extends OnlineUser> T VerifyAndParse(String token, int userId, TokenType type,
	                                               @Nullable String ip)
			throws SessionExceptions {
		T user = redisMemoryService.getDataTheValidityTimeGreater(
				buildCacheKey(token, TOKEN_CACHE_DATA), 5);
		if (user != null)
			return user;
		// 1. 基础令牌验证
		if (!jwtUtil.isTokenUsable(token)) {
			throw new OperationException("令牌已过期");
		}
		
		// 2. 解析JWT内容
		if (type == null) type = TokenType.DEFAULT_STATUS;
		Class<T> clazz = (Class<T>) type.getTokenClazz();
		ReturnResult<T> result = jwtUtil.parseToken(token, SystemConstant.JWT_TOKEN_NAME, clazz);
		
		if (userId == -1)//为-1时认为用户ID未知跳过ID校验
			userId = result.getReturnResult().getUserId();
		if (result.isHasError()) {
			throw new OperationException("令牌解析失败: " + result.getMsg());
		}
		
		T onlineUser = result.getReturnResult();
		
		if (-1 != userId && !TokenType.DEFAULT_STATUS.equals(type))
			// 3. 关键信息校验
			validateTokenConsistency(userId, type, ip, onlineUser);
		
		ReentrantLock lock = getLockForUser(onlineUser.getUserId());
		try {
			lock.lock();
			// 4. 数据库令牌验证
			List<UserToken> validTokens = userTokenMapper.verifyToken(token, userId, type.getStatusNum());
			validateTokenCount(validTokens);
			setTokenCache(token, onlineUser, TOKEN_CACHE_DATA);
			return onlineUser;
		} finally {
			unlockForUser(lock, onlineUser.getUserId());
		}
	}
	
	private void validateTokenCount(List<UserToken> validTokens) throws SessionExceptions {
		if (validTokens.size() != 1) {
			if (validTokens.size() > 1)
				throw new SessionExceptions("错误!您存在多个会话信息,请联系管理员确认!", ErrorEnum.SESSION_DIFFERENT_ERROR);
			else
				throw new SessionExceptions("您的会话已过期,请重新登录后在试一次", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
		}
	}
	
	private void validateTokenConsistency(int userId, TokenType type,
	                                      @Nullable String ip,
	                                      OnlineUser onlineUser) throws SessionExceptions {
		if (onlineUser == null || type == null) {
			throw new SessionExceptions("令牌不匹配", ErrorEnum.SESSION_TOKEN_ERROR);
		}
		// 用户ID和类型校验
		if (onlineUser.getUserId() != userId ||
		    !type.equals(onlineUser.getTokenType())) {
			throw new SessionExceptions("令牌不匹配", ErrorEnum.SESSION_TOKEN_ERROR);
		}
		
		if (!ElseUtil.equalsIp(ip, onlineUser.getIp())) {
			if (!(MyStringUtil.isNull(onlineUser.getIp())))//如果数据库一方没记录就不算变更
				throw new SessionExceptions("IP变更", ErrorEnum.SESSION_IP_CHANGED);
		}
		
	}
	
	private <T extends OnlineUser> UserToken maybeRefreshToken(String oldToken, UserToken userToken) {
		if (!jwtUtil.isAboutToExpire(oldToken)) {
			return userToken;
		}
		// 加锁防止并发刷新
		ReentrantLock lock = getLockForUser(userToken.getUserId());
		try {
			lock.lock();
			// 双重检查锁模式
			Class<T> c = (Class<T>) TokenType.getTokenType(userToken.getType()).getTokenClazz();
			String newToken = jwtUtil.getTokenUsableTime(oldToken, SystemConstant.JWT_TOKEN_NAME, c);
			userToken.setToken(newToken);
			userToken.setDieTime(jwtUtil.getTokenExpireTime(newToken));
			if (userToken.getTokenId() == null)
				throw new OperationException("错误!您的会话令牌更新失败!请联系管理员或者重新进行登录");
			WriteError.tryWrite(userTokenMapper.updateByPrimaryKeySelective(userToken));
			delTokenCache(oldToken);//清空旧的,缓存另外一个,因为想拿到另一个对象还需要重新解析密文所以等用到了再解析
			setTokenCache(userToken.getToken(), userToken, TOKEN_CACHE_USER_TOKEN);
			return userToken;
		} finally {
			unlockForUser(lock, userToken.getUserId());
		}
	}
	
	@Override
	public UserToken verifyToken(String token, int userId, @Nullable String ip, boolean needUpdate) throws SessionExceptions {
		return verifyToken(token, userId, TokenType.DEFAULT_STATUS, ip, needUpdate);
	}
	
	@Override
	public UserToken invalidateToken(String token, int userId, TokenType type) throws SessionExceptions {
		if (MyStringUtil.isNull(token))
			throw new OperationException("错误!!令牌为空!");
		// 令牌验证保证用户是登录的,令牌失效后需要重新登录
		UserToken userToken = verifyToken(token, userId, type, null, false);
		//应该是有效的登录用户,并且传入的id,type和token一致
		int i = userTokenMapper.invalidateToken(token, userId, type.getStatusNum(), new Date(114514));
		if (i != 1) {
			if (i == 0)
				throw new SessionExceptions("错误!您的会话信息不存在,请联系管理员确认!", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
			else
				throw new SessionExceptions("错误!您当前存在多个会话信息!请联系管理员进行解除", ErrorEnum.SESSION_REPEAT_ERROR);
		}
		userToken.setDieTime(new Date(0));
		delTokenCache(token);
		return userToken;
	}
	
	@Override
	public UserToken invalidateToken(String token, int userId) throws SessionExceptions {
		return invalidateToken(token, userId, TokenType.DEFAULT_STATUS);
	}
	
	
	@Override
	public UserToken insertToken(OnlineUser onlineUser, TokenType type, @Nullable long time) throws SessionExceptions {
		//可能需要覆盖原有失效令牌
		if (type == null) type = TokenType.DEFAULT_STATUS;
		if (onlineUser == null) throw new OperationException("令牌生成失败!");
		if (time <= 0)
			time = jwtConfig.getExpirationTime();
		ReentrantLock lock = getLockForUser(onlineUser.getUserId());
		try {
			lock.lock();
			List<UserToken> tokens = userTokenMapper.getTokens(onlineUser.getUserId(), type.getStatusNum());
			if (tokens.size() > 1)
				throw new SessionExceptions("错误!您当前存在多个会话信息!请联系管理员进行解除", ErrorEnum.SESSION_REPEAT_ERROR);
			if (tokens.size() == 1) {
				UserToken userToken = tokens.getFirst();
				userToken.setToken(jwtUtil.generateToken(SystemConstant.JWT_TOKEN_NAME, onlineUser, time));
				userToken.setDieTime(jwtUtil.getTokenExpireTime(userToken.getToken()));
				WriteError.tryWrite(userTokenMapper.updateByPrimaryKeySelective(userToken));
				setTokenCache(userToken.getToken(), userToken, TOKEN_CACHE_USER_TOKEN);
				return userToken;
			} else {
				String s = jwtUtil.generateToken(SystemConstant.JWT_TOKEN_NAME, onlineUser, time);
				UserToken userToken = new UserToken();
				userToken.setUserId(onlineUser.getUserId());
				userToken.setToken(s);
				userToken.setDieTime(jwtUtil.getTokenExpireTime(s));
				userToken.setType(type.getStatusNum());
				WriteError.tryWrite(userTokenMapper.insertSelective(userToken));
				return userToken;
			}
		} finally {
			unlockForUser(lock, onlineUser.getUserId());
		}
	}
}
