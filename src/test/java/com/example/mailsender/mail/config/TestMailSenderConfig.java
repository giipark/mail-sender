package com.example.mailsender.mail.config;

import com.example.mailsender.mail.support.FakeJavaMailSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

@TestConfiguration
public class TestMailSenderConfig {

    @Bean
    @Primary
    public JavaMailSender fakeJavaMailSender() {
        FakeJavaMailSender sender = new FakeJavaMailSender();
        sender.setPerMailDelayMillis(10); // 메일 한 건당 10ms 정도 딜레이
        return sender;
    }
}
