package com.example.mailsender.mail.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MailRequestTest {

    @Test
    @DisplayName("MailRequest 생성 시 기본 상태는 READY이고 재시도 횟수는 0이다")
    void createMailRequestDefaultState() {
        MailRequest mailRequest = MailRequest.builder()
                .toEmail("test@example.com")
                .subject("subject")
                .body("body")
                .build();

        assertEquals(MailStatus.READY, mailRequest.getStatus());
        assertEquals(0, mailRequest.getRetryCount());
    }

    @Test
    @DisplayName("메일을 발송 완료로 변경하면 상태는 SENT이고 sentAt이 설정된다")
    void markAsSent() {
        MailRequest mailRequest = MailRequest.builder()
                .toEmail("test@example.com")
                .subject("subject")
                .body("body")
                .build();

        mailRequest.markAsSent();

        assertEquals(MailStatus.SENT, mailRequest.getStatus());
        assertNotNull(mailRequest.getSentAt());
        assertNull(mailRequest.getFailedReason());
    }

    @Test
    @DisplayName("메일 발송 실패 시 상태는 FAILED가 되고 실패 사유가 저장된다")
    void markAsFailed() {
        MailRequest mailRequest = MailRequest.builder()
                .toEmail("test@example.com")
                .subject("subject")
                .body("body")
                .build();

        mailRequest.markAsFailed("SMTP error");

        assertEquals(MailStatus.FAILED, mailRequest.getStatus());
        assertEquals("SMTP error", mailRequest.getFailedReason());
    }
}
