package com.example.mailsender.mail.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "mail_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK")
    private Long id;

    @Comment("수신 이메일 주소")
    @Column(name = "to_email", nullable = false, length = 255)
    private String toEmail;

    @Comment("메일 제목")
    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Lob
    @Comment("메일 본문")
    @Column(name = "body", nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Comment("메일 상태 (READY, SENDING, SENT, FAILED)")
    @Column(name = "status", nullable = false, length = 20)
    private MailStatus status;

    @Comment("메일 재시도 횟수")
    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Comment("생성 시간")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Comment("수정 시간")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Comment("메일 발송 완료 시각")
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Comment("메일 발송 실패 사유")
    @Column(name = "failed_reason", length = 500)
    private String failedReason;

    @Builder
    private MailRequest(String toEmail, String subject, String body) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.body = body;
        this.status = MailStatus.READY;
        this.retryCount = 0;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsSending() {
        this.status = MailStatus.SENDING;
    }

    public void markAsSent() {
        this.status = MailStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.failedReason = null;
    }

    public void markAsFailed(String reason) {
        this.status = MailStatus.FAILED;
        this.failedReason = reason;
    }

    public void increaseRetryCount() {
        this.retryCount++;
    }

    public boolean canRetry(int maxRetry) {
        return this.status == MailStatus.FAILED && this.retryCount < maxRetry;
    }

    public void updateStatus(MailStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSentSuccess() {
        this.status = MailStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateFailed(String msg) {
        this.status = MailStatus.FAILED;
        this.failedReason = msg;
        this.retryCount += 1;
        this.updatedAt = LocalDateTime.now();
    }

}
