package com.huaji.galgamebyhuaji.constant;

import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class GlobalLock {
    private static final long EXPIRE_TIME_MILLIS = 2 * 60 * 1000;
    
    private static class TimedLock {
        private final ReentrantLock lock = new ReentrantLock();
        private volatile long lastLockedTime = 0;
        
        public void lock() {
            lock.lock();
            lastLockedTime = System.currentTimeMillis();
        }
        
        void forceUnlock() {
            lock.unlock();
        }
        
        public void unlock() {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        
        public boolean isIdle() {
            return !lock.isLocked();
        }
        
        public boolean isHeldTooLong(long maxMillis) {
            return lock.isLocked() && System.currentTimeMillis() - lastLockedTime > maxMillis;
        }
    }
    
    private static final ConcurrentHashMap<Integer, TimedLock> userLocks = new ConcurrentHashMap<>();
    
    /**
     * 仅用于敏感操作，确保最终 释放锁
     */
    public static ReentrantLock getLockForUser(int userId) {
        return userLocks.computeIfAbsent(userId, k -> new TimedLock()).lock;
    }
    
    public static void unlockForUser(ReentrantLock reentrantLock, int userId) {
        TimedLock timedLock = userLocks.get(userId);
        if (timedLock != null && timedLock.lock == reentrantLock) {
            timedLock.unlock();
            userLocks.remove(userId, timedLock);
        }
    }
    
    /**
     * 保留原有安全操作
     */
    public static void safeOperation(Runnable action, int userId) {
        TimedLock lockWrapper = userLocks.computeIfAbsent(userId, k -> new TimedLock());
        try {
            lockWrapper.lock();
            action.run();
        } finally {
            lockWrapper.unlock();
            userLocks.remove(userId, lockWrapper);
        }
    }
    
    /**
     * 新增泛型 Lambda 安全执行，支持事务同步
     */
    public static <T> T safeExecute(int userId, Supplier<T> action) {
        TimedLock lockWrapper = userLocks.computeIfAbsent(userId, k -> new TimedLock());
        lockWrapper.lock();
        
        try {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                // 事务同步，延迟释放锁到事务完成
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        lockWrapper.unlock();
                        userLocks.remove(userId, lockWrapper);
                    }
                });
                return action.get(); // lambda 执行，锁在事务期间保持
            } else {
                // 无事务，执行完立即释放锁
                try {
                    return action.get();
                } finally {
                    lockWrapper.unlock();
                    userLocks.remove(userId, lockWrapper);
                }
            }
        } catch (RuntimeException e) {
            // 异常情况保证释放
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                lockWrapper.unlock();
                userLocks.remove(userId, lockWrapper);
            }
            throw e;
        }
    }
    
    /**
     * 清理长时间占用的锁
     */
    public static void cleanExpiredLocks() {
        userLocks.forEach((userId, timedLock) -> {
            if (timedLock.isHeldTooLong(EXPIRE_TIME_MILLIS)) {
                synchronized (timedLock) {
                    if (timedLock.isHeldTooLong(EXPIRE_TIME_MILLIS)) {
                        timedLock.forceUnlock();
                        userLocks.remove(userId, timedLock);
                        MyLogUtil.info(GlobalLock.class,
                                       "强制清理锁 [userId={%d}, 占用时间={%s}ms]".formatted(
                                               userId, System.currentTimeMillis() - timedLock.lastLockedTime
                                       )
                        );
                    }
                }
            }
        });
    }
}
