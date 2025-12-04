package com.example.mailsender.mail.dto;

import com.example.mailsender.mail.domain.MailStatus;

import java.time.LocalDateTime;

public record MailRequestResponseDto(
        Long id,
        String toEmail,
        String subject,
        String body,
        MailStatus status,
        int retryCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime sentAt,
        String failedReason
) {
}
