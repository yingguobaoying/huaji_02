package com.huaji.galgamebyhuaji.myUtil;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;

import java.util.concurrent.TimeUnit;

public class JWTTokenCacheUtil {
	
	private static final int MAX_CACHE_SIZE = 100;
	private static final int EXPIRE_SECONDS = 30;
	
	private static final Cache<String, OnlineUser> cache = Caffeine.newBuilder()
			.maximumSize(MAX_CACHE_SIZE)
			.expireAfterWrite(EXPIRE_SECONDS, TimeUnit.SECONDS)
			.build();
	
	public static void setCache (String token, OnlineUser user) {
		if ( token == null || user == null ) return;
		String key = MyStringUtil.encryption(token);
		cache.put(key, user);
	}
	
	public static OnlineUser getCache (String token) {
		if ( token == null ) return null;
		String key = MyStringUtil.encryption(token);
		OnlineUser user = cache.getIfPresent(key);
		if ( user != null ) {
			cache.invalidate(key); // 一次性缓存
		}
		return user;
	}
}