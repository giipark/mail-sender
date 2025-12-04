package com.example.mailsender.mail.repository;

import com.example.mailsender.mail.domain.MailSendLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailSendLogRepository extends JpaRepository<MailSendLog, Long> {

    List<MailSendLog> findByMailRequestIdOrderByLoggedAtAsc(Long mailRequestId);
}
