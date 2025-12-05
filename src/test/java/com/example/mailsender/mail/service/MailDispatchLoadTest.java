package com.example.mailsender.mail.service;

import com.example.mailsender.mail.config.TestMailSenderConfig;
import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.repository.MailRequestRepository;
import com.example.mailsender.mail.support.FakeJavaMailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {TestMailSenderConfig.class})
class MailDispatchLoadTest {

    @Autowired
    private MailRequestService mailRequestService;

    @Autowired
    private MailRequestRepository mailRequestRepository;

    @Autowired
    private ApplicationContext applicationContext;

    private FakeJavaMailSender getFakeSender() {
        return (FakeJavaMailSender) applicationContext.getBean("fakeJavaMailSender");
    }

    @Test
    @DisplayName("READY 메일 100, 500, 1000건 비동기 발송 부하 테스트")
    void dispatchReadyMails_loadTest() throws Exception {
        int[] sizes = {100, 500, 1000};

        for (int size : sizes) {
            // 초기화
            mailRequestRepository.deleteAll();
            getFakeSender().getSendCount().set(0);

            // READY 상태 메일 N건 생성
            createReadyMails(size);

            long start = System.currentTimeMillis();

            // 디스패치 실행
            int dispatchedCount = mailRequestService.dispatchReadyMails();

            // 지정 시간 안에 sendCount == size 되는지 기다리기
            waitUntilAllSent(size, 30_000); // 최대 30초까지 기다림

            long elapsed = System.currentTimeMillis() - start;

            System.out.println("===== size = " + size + " =====");
            System.out.println("dispatchedCount = " + dispatchedCount);
            System.out.println("sendCount       = " + getFakeSender().getSendCount().get());
            System.out.println("elapsed(ms)     = " + elapsed);

            assertThat(dispatchedCount).isEqualTo(size);
            assertThat(getFakeSender().getSendCount().get()).isEqualTo(size);
        }
    }

    @Transactional
    void createReadyMails(int size) {
        List<MailRequest> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MailRequest mail = MailRequest.builder()
                    .toEmail("user" + i + "@example.com")
                    .subject("테스트 메일 " + i)
                    .body("내용 " + i)
                    .build();
            list.add(mail);
        }
        mailRequestRepository.saveAll(list);
    }

    private void waitUntilAllSent(int expected, long timeoutMillis) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMillis) {
            int current = getFakeSender().getSendCount().get();
            if (current >= expected) {
                return;
            }
            Thread.sleep(200); // 0.2초 간격으로 폴링
        }
        // 타임아웃까지 못 채우면 테스트에서 실패하게 만들고 싶으면 assert 추가
    }
}
