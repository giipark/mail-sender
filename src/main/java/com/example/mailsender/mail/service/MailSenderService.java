package com.example.mailsender.mail.service;

import com.example.mailsender.mail.domain.MailRequest;

/**
 * 실제 메일 전송을 담당하는 인터페이스
 */
public interface MailSenderService {

    void sendMail(MailRequest mailRequest);
}
