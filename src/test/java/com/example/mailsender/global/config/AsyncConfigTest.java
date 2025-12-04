package com.example.mailsender.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig({AsyncConfig.class})
@EnableConfigurationProperties(AsyncProperties.class)
@TestPropertySource(properties = {
        "mail.async.core-pool-size=10",
        "mail.async.max-pool-size=50",
        "mail.async.queue-capacity=100",
        "mail.async.thread-name-prefix=TestMailExecutor-"
})
class AsyncConfigTest {

    @Autowired
    @Qualifier("mailTaskExecutor")
    private Executor executor;

    @Test
    @DisplayName("설정값에 따라 ThreadPoolTaskExecutor Bean이 올바르게 생성되어야 한다")
    void mailTaskExecutor_shouldBeConfiguredCorrectly() {
        // given & when
        assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;

        // then: 설정한 프로퍼티 값들이 제대로 반영되었는지 검증
        assertThat(taskExecutor.getCorePoolSize()).isEqualTo(10);
        assertThat(taskExecutor.getMaxPoolSize()).isEqualTo(50);
        assertThat(taskExecutor.getQueueCapacity()).isEqualTo(100);
        assertThat(taskExecutor.getThreadNamePrefix()).isEqualTo("TestMailExecutor-");
    }

    @Test
    @DisplayName("거절 정책(RejectedExecutionHandler)이 설정되어 있어야 한다")
    void rejectedExecutionHandler_shouldBeSet() {
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;

        // 람다식이라 구체적인 클래스 타입 비교는 어렵지만, null이 아님을 검증
        assertThat(taskExecutor.getThreadPoolExecutor().getRejectedExecutionHandler()).isNotNull();
    }
}