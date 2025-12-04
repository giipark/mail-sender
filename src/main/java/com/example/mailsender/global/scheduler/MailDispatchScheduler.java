package com.example.mailsender.global.scheduler;

import com.example.mailsender.global.config.MailDispatchProperties;
import com.example.mailsender.mail.service.MailRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class MailDispatchScheduler {

    @Value("${mail.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    private final MailRequestService mailRequestService;

    private final MailDispatchProperties mailDispatchProperties;

    /**
     * 10초마다 READY 상태 메일을 최대 100개씩 발송 트리거한다.
     * fixedDelay = 이전 작업 종료 후 10초 뒤에 실행
     */
    @Scheduled(fixedDelay = 60_000)  // 60초
    public void dispatchReadyMails() {
        if (!schedulerEnabled) return;

        int dispatchedCount = mailRequestService.dispatchReadyMails();

        if (dispatchedCount > 0) {
            log.info("[SCHEDULER] READY {}건 발송 처리", dispatchedCount);
        }
    }
}
