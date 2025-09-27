package com.huaji.galgamebyhuaji.myUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaji.galgamebyhuaji.config.JWTConfig;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JWTUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String ISSUER = "galGameByHuaJi";

	private final JWTConfig jwtConfig;

	private final AESEncryptionUtil aesUtil;

	/**
	 * 生成 JWT 令牌（对象内容先加密再存储，使用当前版本密钥）
	 */
	public String generateToken(String key, Object encryptedObj, long expirationTime) {
		try {
			byte[] signingKey = jwtConfig.getCurrentSigningKey();
			Algorithm algorithm = Algorithm.HMAC256(signingKey);
			Date expireDate = new Date(System.currentTimeMillis() + expirationTime);

			String json = objectMapper.writeValueAsString(encryptedObj);
			String encryptedJson = aesUtil.encryptValue(json);

			return JWT.create()
					.withIssuer(ISSUER)
					.withExpiresAt(expireDate)
					.withClaim(key, encryptedJson)
					.sign(algorithm);
		} catch (Exception e) {
			MyLogUtil.error(JWTUtil.class, "Token生成失败", e);
			return null;
		}
	}

	/**
	 * 检查令牌是否将在5分钟内过期
	 */
	public boolean isAboutToExpire(String token) {
		try {
			DecodedJWT jwt = verifyToken(token);
			Date expiresAt = jwt.getExpiresAt();
			return expiresAt != null && (expiresAt.getTime() - System.currentTimeMillis()) <= 5 * 60 * 1000;
		} catch (Exception e) {
			MyLogUtil.error(JWTUtil.class, "判断是否即将过期失败", e);
			return false;
		}
	}

	/**
	 * 获取令牌过期时间
	 */
	public Date getTokenExpireTime(String token) {
		try {
			DecodedJWT jwt = verifyToken(token);
			return jwt.getExpiresAt();
		} catch (Exception e) {
			MyLogUtil.error(JWTUtil.class, "判断是否即将过期失败", e);
			return null;
		}
	}

	/**
	 * 验证令牌是否有效（未过期且未被篡改）
	 */
	public boolean isTokenUsable(String token) {
		try {
			verifyToken(token);
			return true;
		} catch (Exception e) {
			MyLogUtil.error(JWTUtil.class, "令牌无效: " + e.getMessage());
			return false;
		}
	}

	/**
	 * 延长 JWT 令牌的有效时间（重新生成新 Token）
	 * 延长的时间为当前默认有效时间
	 */
	public <T> String getTokenUsableTime(String token, String key, Class<T> clazz) {
		ReturnResult<T> result = parseToken(token, key, clazz);
		if (!result.isOperationResult()) {
			return null;
		}
		return generateToken(key, result.getReturnResult(), jwtConfig.getExpirationTime());
	}

	public JWTUtil(JWTConfig jwtConfig, AESEncryptionUtil aesUtil) {
		this.jwtConfig = jwtConfig;
		this.aesUtil = aesUtil;
	}

	/**
	 * 解析 JWT，获取 claim 中加密的对象并解密
	 */
	public <T> ReturnResult<T> parseToken(String token, String key, Class<T> clazz) {
		try {
			DecodedJWT jwt = verifyToken(token);
			String encryptedJson = jwt.getClaim(key).asString();

			if (encryptedJson == null) {
				return ReturnResult.isError("Token 中未找到对应的加密内容");
			}

			String decryptedJson = aesUtil.decryptValue(encryptedJson);
			T obj = objectMapper.readValue(decryptedJson, clazz);
			return ReturnResult.isTrue("Token解析成功", obj);
		} catch (JWTVerificationException e) {
			MyLogUtil.error(JWTUtil.class, "Token 验证失败", e);
			return ReturnResult.isError("Token 无效或已过期: " + e.getMessage());
		} catch (Exception e) {
			MyLogUtil.error(JWTUtil.class, "Token 解析异常", e);
			return ReturnResult.isError("Token解析异常: " + e.getMessage());
		}
	}

	/**
	 * 内部使用：验证并返回解码后的 JWT 对象（支持密钥轮换）
	 */
	private DecodedJWT verifyToken(String token) {
		List<byte[]> activeKeys = jwtConfig.getActiveVerificationKeys();

		JWTVerificationException lastException = null;

		for (byte[] key : activeKeys) {
			try {
				Algorithm algorithm = Algorithm.HMAC256(key);
				JWTVerifier verifier = JWT.require(algorithm)
						.withIssuer(ISSUER)
						.build();
				return verifier.verify(token);
			} catch (JWTVerificationException e) {
				lastException = e; // 记录最后一次异常
				MyLogUtil.error(JWTUtil.class, "Token 验证时出现错误:", e);
			}
		}
		throw lastException != null ? lastException : new JWTVerificationException("Token 验证失败" + lastException);
	}
}
