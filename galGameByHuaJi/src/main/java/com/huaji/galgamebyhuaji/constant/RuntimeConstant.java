package com.huaji.galgamebyhuaji.constant;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RuntimeConstant {
	/**
	 * 一个资源最多包括的标签数量
	 */
	private static int maxResourceTagSize = 0;
	private final static ReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	public static int getMaxResourceTagSize () {
		try {
			rwLock.readLock().lock();
			return maxResourceTagSize;
		} finally {
			rwLock.readLock().unlock();
		}
	}
	
	public static void setMaxResourceTagSize (int maxResourceTagSize) {
		try {
			rwLock.writeLock().lock();
			RuntimeConstant.maxResourceTagSize = maxResourceTagSize;
		} finally {
			rwLock.writeLock().unlock();
		}
	}
}