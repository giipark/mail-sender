package com.example.mailsender.mail.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "mail_send_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailSendLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("메일 발송 로그 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mail_request_id")
    @Comment("연결된 메일 요청 ID")
    private MailRequest mailRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Comment("발송 상태 (READY, SENDING, SENT, FAILED)")
    private MailStatus status;

    @Column(name = "message", length = 500)
    @Comment("발송 메시지 또는 오류 메시지")
    private String message;

    @Column(name = "logged_at", nullable = false)
    @Comment("로그 기록 시각")
    private LocalDateTime loggedAt;

    @Builder
    private MailSendLog(MailRequest mailRequest, MailStatus status, String message) {
        this.mailRequest = mailRequest;
        this.status = status;
        this.message = message;
        this.loggedAt = LocalDateTime.now();
    }

    public static MailSendLog success(MailRequest mailRequest, String message) {
        return MailSendLog.builder()
                .mailRequest(mailRequest)
                .status(MailStatus.SENT)
                .message(message)
                .build();
    }

    public static MailSendLog failure(MailRequest mailRequest, String message) {
        return MailSendLog.builder()
                .mailRequest(mailRequest)
                .status(MailStatus.FAILED)
                .message(message)
                .build();
    }
}
