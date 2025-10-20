package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.Tag;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import io.micrometer.common.lang.Nullable;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class RedisMemoryService {
	private final String PREFIX_USER = "user:";
	private final String PREFIX_RESOURCES = "resources:";
	private final String PREFIX_TAG = "tag:";
	private final String PREFIX_RESOURCES_LIST = "ResourcesSortedIds";
	private final String TAG_MAP = "tagMap";
	
	@Autowired
	private RedissonClient redissonClient;
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	/**
	 * 保存对象到 Redis，自动根据类型选择策略
	 */
	public <T> void saveData(T value) {
		try {
			rwLock.writeLock().lock();
			if (value == null) throw new OperationException("操作对象不可为空!");
			
			switch (value) {
				case Users users -> redissonClient.getBucket(PREFIX_USER + users.getUserId()).set(value);
				
				case Resources resources -> {
					int newId = resources.getrId();
					RList<Integer> sortedIdList = redissonClient.getList(PREFIX_RESOURCES_LIST);
					if (sortedIdList.isEmpty() || newId > sortedIdList.getLast()) {
						sortedIdList.add(newId);
					} else {
						int index = Collections.binarySearch(sortedIdList, newId);
						if (index < 0) index = -index - 1;
						sortedIdList.add(index, newId);
					}
					redissonClient.getBucket(PREFIX_RESOURCES + resources.getrId()).set(resources);
				}
				
				case Tag tag -> {
					redissonClient.getBucket(PREFIX_TAG + tag.getTagId()).set(value);
					redissonClient.getMap(TAG_MAP).put(tag.getTagId(), tag);
				}
				
				default -> throw new OperationException("不支持的类型: " + value.getClass());
			}
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	
	/**
	 * 获取数据（需要提供类型）
	 */
	public <T> T getData(Integer key, Class<T> clazz) {
		try {
			rwLock.readLock().lock();
			if (clazz == null) throw new OperationException("操作对象不可为空!");
			
			if (clazz == Users.class) return (T) redissonClient.getBucket(PREFIX_USER + key).get();
			if (clazz == Resources.class) return (T) redissonClient.getBucket(PREFIX_RESOURCES + key).get();
			if (clazz == Tag.class) return (T) redissonClient.getBucket(PREFIX_TAG + key).get();
			
			throw new OperationException("不支持的类型: " + clazz);
		} finally {
			rwLock.readLock().unlock();
		}
	}
	
	/**
	 * 删除 Redis key
	 */
	public boolean deleteKey(Integer key, Class<?> clazz) {
		try {
			rwLock.writeLock().lock();
			if (clazz == null) throw new OperationException("操作对象不可为空!");
			
			if (clazz == Users.class) {
				return redissonClient.getBucket(PREFIX_USER + key).delete();
			}
			if (clazz == Resources.class) {
				redissonClient.getList(PREFIX_RESOURCES_LIST).remove(key);
				return redissonClient.getBucket(PREFIX_RESOURCES + key).delete();
			}
			if (clazz == Tag.class) {
				redissonClient.getMap(TAG_MAP).remove(key);
				return redissonClient.getBucket(PREFIX_TAG + key).delete();
			}
			
			throw new OperationException("不支持的类型: " + clazz);
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	
	/**
	 * 初始化方法，仅在服务器启动时调用
	 */
	public void setKey(List<Resources> list) {
		if (list == null || list.isEmpty()) return;
		
		RBatch batch = redissonClient.createBatch();
		list.forEach(res -> batch.getBucket(PREFIX_RESOURCES + res.getrId()).setAsync(res));
		batch.execute();
		
		// 维护资源 ID 排序列表
		List<Integer> sortedIdList = list.stream()
				.map(Resources::getrId)
				.sorted()
				.toList();
		RList<Integer> redisList = redissonClient.getList(PREFIX_RESOURCES_LIST);
		redisList.clear();
		redisList.addAll(sortedIdList);
	}
	
	/**
	 * 初始化方法，仅在服务器启动时调用
	 */
	public void setKey(Map<Integer, ?> map) {
		if (map == null || map.isEmpty()) return;
		Object value = map.values().iterator().next();
		RBatch batch = redissonClient.createBatch();
		
		switch (value) {
			case Users ignored -> map.forEach((id, res) -> batch.getBucket(PREFIX_USER + id).setAsync(res));
			
			case Tag ignored -> {
				RMap<Integer, Tag> redisMap = redissonClient.getMap(TAG_MAP);
				redisMap.clear();
				redisMap.putAll((Map<Integer, Tag>) map);
				map.forEach((tagId, res) -> batch.getBucket(PREFIX_TAG + tagId).setAsync(res));
			}
			
			case null -> throw new OperationException("操作对象不可为空!");
			default -> throw new OperationException("不支持的类型: " + value.getClass());
		}
		batch.execute();
	}
	
	public Map<Integer, Tag> getTagMap() {
		try {
			rwLock.readLock().lock();
			return redissonClient.getMap(TAG_MAP);
		} finally {
			rwLock.readLock().unlock();
		}
	}
	
	/**
	 * 分页查询资源
	 */
	public List<Resources> getPagedResources(int start, int end) {
		if (start < 0 || end < start) {
			throw new OperationException("错误的分页范围!");
		}
		try {
			rwLock.readLock().lock();
			RList<Integer> redisIdList = redissonClient.getList(PREFIX_RESOURCES_LIST);
			long size = redisIdList.size();
			if (start >= size) {
				throw new OperationException("错误的分页范围! 起始索引超出总记录数");
			}
			int toIndex = (int) Math.min(end + 1, size);
			
			List<Integer> idList = redisIdList.range(start, toIndex - 1);
			
			// 批量异步获取
			RBatch batch = redissonClient.createBatch();
			idList.forEach(id -> batch.getBucket(PREFIX_RESOURCES + id).getAsync());
			BatchResult<?> batchResult = batch.execute();
			
			List<Resources> result = new ArrayList<>(idList.size());
			for (Object obj : batchResult.getResponses()) {
				if (obj instanceof Resources res) {
					result.add(getConvertingListResources(res));
				}
			}
			return result;
		} finally {
			rwLock.readLock().unlock();
		}
	}
	
	private Resources getConvertingListResources(Resources resources) {
		if (resources == null) return null;
		Resources res = new Resources();
		res.setrId(resources.getrId());
		res.setrName(resources.getrName());
		res.setrManufacturer(resources.getrManufacturer());
		res.setTags(resources.getTags());
		res.setrJpeg(resources.getrJpeg());
		return res;
	}
	
	public int getResourceListSize() {
		try {
			rwLock.readLock().lock();
			return redissonClient.getList(PREFIX_RESOURCES_LIST).size();
		} finally {
			rwLock.readLock().unlock();
		}
	}
	
	/**
	 * 清理当前业务数据，避免误删其他库数据
	 */
	public void delAllData() {
		RKeys keys = redissonClient.getKeys();
		keys.deleteByPattern(PREFIX_USER + "*");
		keys.deleteByPattern(PREFIX_RESOURCES + "*");
		keys.deleteByPattern(PREFIX_TAG + "*");
		keys.delete(PREFIX_RESOURCES_LIST);
		keys.delete(TAG_MAP);
		keys.deleteByPattern(TokenService.TOKEN_CACHE_KEY + ":*");
	}
	
	private final ConcurrentHashMap<Integer, ReadWriteLock> keyLocks = new ConcurrentHashMap<>();
	
	public <T> void saveData(String name, T t, Date time) {
		ReadWriteLock keyLock = keyLocks.computeIfAbsent(name.hashCode(), k -> new ReentrantReadWriteLock());
		try {
			keyLock.writeLock().lock();
			RBucket<T> bucket = redissonClient.getBucket(name);
			// 设置过期时间(绝对时间)
			if (time != null) {
				// 计算剩余时间（毫秒）
				long expireTime = time.getTime() - System.currentTimeMillis();
				if (expireTime > 0) {
					Duration duration = Duration.ofMillis(expireTime);
					bucket.set(t, duration);
				}else
					bucket.delete();
				
			} else {
				// 如果没有指定过期时间，存储为永不过期
				bucket.set(t);
			}
		} finally {
			keyLock.writeLock().unlock();
		}
	}
	
	public <T> T getDataTheValidityTimeGreater(String key, int min) {
		ReadWriteLock keyLock = keyLocks.computeIfAbsent(key.hashCode(), k -> new ReentrantReadWriteLock());
		try {
			keyLock.readLock().lock();
			RBucket<T> bucket = redissonClient.getBucket(key);
			long ttl = bucket.remainTimeToLive();
			if (ttl == -2) {
				return null;
			}
			long minTtlRequired = 1000L * 60 * min;
			if (ttl == -1 || ttl >= minTtlRequired) {
				return bucket.get();
			}
			//删除并返回空
			delData(key);
			return null;
		} finally {
			keyLock.readLock().unlock();
		}
	}
	
	@Nullable
	public <T> T getData(String name, Class<T> t) {
		ReadWriteLock keyLock = keyLocks.computeIfAbsent(name.hashCode(), k -> new ReentrantReadWriteLock());
		try {
			keyLock.readLock().lock();
			return (T) redissonClient.getBucket(name).get();
		} finally {
			keyLock.readLock().unlock();
		}
	}
	
	public void delData(String name) {
		ReadWriteLock keyLock = keyLocks.computeIfAbsent(name.hashCode(), k -> new ReentrantReadWriteLock());
		try {
			keyLock.writeLock().lock();
			redissonClient.getBucket(name).delete();
		} finally {
			keyLock.writeLock().unlock();
		}
	}
}
