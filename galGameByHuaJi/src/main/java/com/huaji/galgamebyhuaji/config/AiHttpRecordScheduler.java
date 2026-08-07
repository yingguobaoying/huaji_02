package com.huaji.galgamebyhuaji.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：定期清理 HTTP 拦截器中的过期缓存记录。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AiHttpRecordScheduler {

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void flushCompletedRecords() {
        int pending = AiHttpRecordInterceptor.pendingCount();
        if (pending > 0) {
            log.debug("flushCompletedRecords: pending={}", pending);
        }
        AiHttpRecordInterceptor.cleanupStale(30);
    }
}
