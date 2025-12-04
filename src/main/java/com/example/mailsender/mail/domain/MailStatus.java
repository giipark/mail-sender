package com.example.mailsender.mail.domain;

public enum MailStatus {
    READY,      // 발송 대기
    SENDING,    // 발송 중
    SENT,       // 발송 완료
    FAILED      // 발송 실패
}
