package com.huaji.galgamebyhuaji.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 占位定时任务。
 * HttpInterceptor 已简化为纯日志记录，不再需要缓存清理。
 * 保留此类以备后续扩展（如 AI 调用限流统计等）。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AiHttpRecordScheduler {
    // 预留扩展点
}