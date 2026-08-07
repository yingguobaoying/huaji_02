package com.huaji.galgamebyhuaji.constant;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * AI 聊天请求专用用户锁。
 * 同一用户同一时间只能有一个 AI 请求在执行，确保 UserAiContext 不会并发篡改。
 */
public class AiUserLock {
    
    private static final ConcurrentMap<Integer, ReentrantLock> LOCKS = new ConcurrentHashMap<>();
    private static final long LOCK_TIMEOUT_MS = 10 * 60 * 1000; // 10 分钟超时
    
    private AiUserLock() {}
    
    /**
     * 获取指定用户的 AI 锁（外部负责 lock/unlock）
     */
    public static ReentrantLock getLock(int userId) {
        return LOCKS.computeIfAbsent(userId, k -> new ReentrantLock());
    }
    
    /**
     * 执行带锁的 AI 操作。
     * 超时 60 秒后自动释放锁，防止死锁。
     */
    public static <T> T executeLocked(int userId, Supplier<T> action) {
        ReentrantLock lock = getLock(userId);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(LOCK_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new RuntimeException("AI请求超时，请稍后重试");
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("AI请求被中断", e);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            // 清理无用锁
            if (!lock.isLocked() && !lock.hasQueuedThreads()) {
                LOCKS.remove(userId, lock);
            }
        }
    }
}
