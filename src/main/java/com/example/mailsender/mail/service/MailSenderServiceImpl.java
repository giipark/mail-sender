package com.example.mailsender.mail.service;

import com.example.mailsender.global.config.MailProperties;
import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.domain.MailSendLog;
import com.example.mailsender.mail.repository.MailSendLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final MailSendLogRepository mailSendLogRepository;

    @Override
    @Transactional
    public void sendMail(MailRequest mailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getDefaultFrom());
        message.setTo(mailRequest.getToEmail());
        message.setSubject(mailRequest.getSubject());
        message.setText(mailRequest.getBody());

        log.debug("Sending email to={}, subject={}", mailRequest.getToEmail(), mailRequest.getSubject());

        try {
            mailSender.send(message);

            // 메일 발송 성공시,
            mailSendLogRepository.save(
                    MailSendLog.success(mailRequest, "메일 전송 성공")
            );

        } catch (Exception e) {
            log.error("메일 전송 실패 to={}, subject={} : {}",
                    mailRequest.getToEmail(), mailRequest.getSubject(), e.getMessage(), e);

            // 메일 발송 실패시,
            mailSendLogRepository.save(
                    MailSendLog.failure(mailRequest, e.getMessage())
            );

            // 상위 레이어에 실패 알려서 MailRequest 상태 업데이트
            throw new IllegalStateException("메일 전송 실패: " + e.getMessage(), e);
        }
    }
}
