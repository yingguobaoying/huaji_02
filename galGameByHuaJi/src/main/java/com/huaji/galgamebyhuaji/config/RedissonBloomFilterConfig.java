package com.huaji.galgamebyhuaji.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonBloomFilterConfig {
	
	private static final String RESOURCE_FILTER = "resourceFilter";
	private static final String MANUFACTURER_FILTER = "manufacturerFilter";
	@Autowired
	private RedissonClient redissonClient;
	
	
	@Bean(name = "RNameBloomFilter")
	RBloomFilter<String> bloomFilter () {
		RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(RESOURCE_FILTER);
		if ( !bloomFilter.isExists() ) {
			bloomFilter.tryInit(100_000L, 0.01);
		}
		return bloomFilter;
	}
	
	@Bean(name = "manufacturerFilterBloomFilter")
	RBloomFilter<String> manufacturerFilterbloomFilter () {
		RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(MANUFACTURER_FILTER);
		if ( !bloomFilter.isExists() ) {
			bloomFilter.tryInit(200L, 0.001);
			
		}
		return bloomFilter;
	}
	
}