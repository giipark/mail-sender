package com.example.mailsender.mail.service;

import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.domain.MailStatus;
import com.example.mailsender.mail.repository.MailRequestRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncMailSenderService {

    private final JavaMailSender javaMailSender;
    private final MailRequestRepository mailRequestRepository;

    @Async("mailTaskExecutor")
    @Transactional
    public void sendMailAsync(Long mailRequestId) {

        MailRequest mail = mailRequestRepository.findById(mailRequestId)
                .orElseThrow(() -> new IllegalArgumentException("메일 요청이 존재하지 않습니다. id=" + mailRequestId));

        try {
            // READY → SENDING
            mail.updateStatus(MailStatus.SENDING);

            // 실제 메일 발송
            sendMailInternal(mail);

            // 성공 처리
            mail.updateSentSuccess();

            log.info("[메일 발송 성공] id={} to={}", mail.getId(), mail.getToEmail());

        } catch (Exception e) {

            log.error("[메일 발송 실패] id={} msg={}", mail.getId(), e.getMessage(), e);

            mail.updateFailed(e.getMessage());
        }
    }

    private void sendMailInternal(MailRequest mail) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(mail.getToEmail());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getBody(), false);

        javaMailSender.send(message);
    }
}
