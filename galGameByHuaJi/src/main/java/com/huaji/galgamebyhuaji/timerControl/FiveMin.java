package com.huaji.galgamebyhuaji.timerControl;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.vignaAiFrame.ChatContextMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FiveMin {
    @Scheduled(fixedRate =  60 * 1000 * 5, initialDelay = 60 * 1000 * 5)
    public void startFrom5m() {
        log.info("开始清理无用资源");
        ChatContextMap.DisposalOfCorpse();
        GlobalLock.cleanExpiredLocks();
        log.info("清理完成");
    }
}
