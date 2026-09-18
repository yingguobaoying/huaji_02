package com.huaji.galgamebyhuaji.timerControl;

import com.huaji.galgamebyhuaji.config.JWTConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class OneWeek {
    private final JWTConfig jwtConfig;
    
    @Scheduled(fixedRate = 24 * 7 * 60 * 60 * 1000, initialDelay = 60 * 60 * 1000 * 24 * 7)
    public void starBy7d() {
        log.info("============开始密钥轮换============");
        int size = jwtConfig.getActiveVerificationKeys().size();
        Random random = new Random();
        int i = random.nextInt(0, size);
        jwtConfig.rotateKey(i);
        log.info("============密钥版本:[{}]============",i);
        log.info("============密钥轮换完成============");
    }
}
