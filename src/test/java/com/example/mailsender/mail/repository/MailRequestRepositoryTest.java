package com.example.mailsender.mail.repository;

import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.domain.MailStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MailRequestRepositoryTest {

    @Autowired
    private MailRequestRepository mailRequestRepository;

    @Test
    @DisplayName("READY 상태의 메일을 생성 시간 기준 상위 100건까지 조회한다")
    void findTop100ByStatusOrderByCreatedAtAsc() {
        // given
        for (int i = 0; i < 3; i++) {
            MailRequest mailRequest = MailRequest.builder()
                    .toEmail("user" + i + "@example.com")
                    .subject("subject " + i)
                    .body("body " + i)
                    .build();
            mailRequestRepository.save(mailRequest);
        }

        // when
        List<MailRequest> result =
                mailRequestRepository.findTop100ByStatusOrderByCreatedAtAsc(MailStatus.READY);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .allMatch(m -> m.getStatus() == MailStatus.READY);
    }
}
