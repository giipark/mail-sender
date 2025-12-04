// src/main/java/com/example/mailsender/mail/service/MailReservationService.java
package com.example.mailsender.mail.service;

import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.domain.MailStatus;
import com.example.mailsender.mail.repository.MailRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailReservationService {

    private final MailRequestRepository mailRequestRepository;

    @Transactional
    public List<MailRequest> reserveReadyMails(int limit) {
        PageRequest pageable = PageRequest.of(0, limit);

        List<MailRequest> readyMails =
                mailRequestRepository.findReadyForUpdate(MailStatus.READY, pageable);

        if (readyMails.isEmpty())
            return readyMails;

        for (MailRequest mail : readyMails) {
            mail.updateStatus(MailStatus.SENDING);
        }

        return readyMails;
    }
}
