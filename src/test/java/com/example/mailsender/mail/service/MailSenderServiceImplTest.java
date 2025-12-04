package com.example.mailsender.mail.service;

import com.example.mailsender.global.config.MailProperties;
import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.repository.MailSendLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MailProperties mailProperties;

    @Mock
    private MailRequest mailRequest;

    @Mock
    private MailSendLogRepository mailSendLogRepository;

    @InjectMocks
    private MailSenderServiceImpl mailSenderService;

    @BeforeEach
    void setUp() {
        mailRequest = MailRequest.builder()
                .toEmail("test@example.com")
                .subject("테스트 메일")
                .body("테스트 메일 본문입니다.")
                .build();
    }

    @Test
    @DisplayName("JavaMailSender를 이용해 단일 텍스트 메일을 전송한다")
    void send_shouldUseJavaMailSender() {
        when(mailProperties.getDefaultFrom()).thenReturn("no-reply@example.com");

        // given
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // when
        mailSenderService.sendMail(mailRequest);

        // then
        verify(javaMailSender).send(captor.capture());
        SimpleMailMessage sentMessage = captor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(mailRequest.getToEmail());
        assertThat(sentMessage.getSubject()).isEqualTo(mailRequest.getSubject());
        assertThat(sentMessage.getText()).isEqualTo(mailRequest.getBody());
    }
}
