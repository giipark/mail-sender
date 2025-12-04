package com.example.mailsender.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

    private final AsyncProperties asyncProperties;

    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(asyncProperties.getCorePoolSize());            // 기본 스레드 수
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());              // 최대 스레드 수
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());          // 큐에 쌓을 수 있는 작업 수
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());

        // 예외 처리 핸들러 등록(옵션)
        executor.setRejectedExecutionHandler((r, e) -> {
            log.error("메일 비동기 작업이 거절되었습니다. pool={}, active={}, queued={}",
                    e.getPoolSize(), e.getActiveCount(), e.getQueue().size());
            throw new RejectedExecutionException("서버가 너무 혼잡합니다.");
        });

        executor.initialize();
        return executor;
    }
}
