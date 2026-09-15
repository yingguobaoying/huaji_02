package com.huaji.galgamebyhuaji.timerControl;

import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.service.RootServlet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OneHour {
    private final RootServlet rootServlet;
    
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void startFrom1h() {
        //顺便把root刷了
        try {
            rootServlet.rootUserInit();
        } catch (SessionExceptions e) {
            log.error("刷新root用户时出错:{}", e.getMessage());
        } catch (Exception e) {
            log.error("刷新root用户时出错", e);
        }
    }
}
