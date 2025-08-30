package com.huaji.galgamebyhuaji.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class RedissonConfig {
	
	@Bean
	public RedissonClient redissonClient () throws IOException {
		Config config = Config.fromYAML(
				RedissonConfig.class.getClassLoader().getResource("redisson.yml"));
		return Redisson.create(config);
	}
	
	@Bean
	public RedisCacheManager cacheManager (RedisConnectionFactory factory) {
		// 创建 ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.activateDefaultTyping(
				LaissezFaireSubTypeValidator.instance,
				ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY
		                                  );
		
		// 直接构造时传入 objectMapper
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
		
		// 缓存配置
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
				.entryTtl(Duration.ofHours(1)); // 缓存时间可调整
		
		return RedisCacheManager.builder(factory)
				.cacheDefaults(config)
				.build();
	}
}