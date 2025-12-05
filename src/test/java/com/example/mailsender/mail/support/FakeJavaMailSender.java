package com.example.mailsender.mail.support;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeJavaMailSender implements JavaMailSender {

    @Getter
    private final AtomicInteger sendCount = new AtomicInteger(0);

    /**
     * 한 건 보낼 때마다 지연 시간 얼마나 줄지 (ms)
     */
    @Setter
    private long perMailDelayMillis = 0L;

    @Override
    public MimeMessage createMimeMessage() {
        // 실제로 메일 안 보낼 거라 그냥 빈 MimeMessage 생성 (Test)
        return new MimeMessage((Session) null);
    }

    @Override
    public MimeMessage createMimeMessage(java.io.InputStream contentStream) {
        try {
            return new MimeMessage(null, contentStream);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void send(MimeMessage mimeMessage) {
        simulateDelay();
        sendCount.incrementAndGet();
    }

    @Override
    public void send(MimeMessage... mimeMessages) {
        for (MimeMessage mimeMessage : mimeMessages) {
            send(mimeMessage);
        }
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) {
        simulateDelay();
        sendCount.incrementAndGet();
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) {
        for (SimpleMailMessage message : simpleMessages) {
            send(message);
        }
    }

    private void simulateDelay() {
        if (perMailDelayMillis > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(perMailDelayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
