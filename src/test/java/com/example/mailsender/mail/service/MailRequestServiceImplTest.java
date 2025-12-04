package com.example.mailsender.mail.service;

import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.domain.MailStatus;
import com.example.mailsender.mail.dto.MailRequestCreateRequestDto;
import com.example.mailsender.mail.mapper.MailRequestMapper;
import com.example.mailsender.mail.repository.MailRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailRequestServiceImplTest {

    @Mock
    private MailRequestRepository mailRequestRepository;

    @Mock
    private MailSenderService mailSenderService;

    @Mock
    private MailRequest mailRequest;

    @Mock
    private MailRequestMapper mailRequestMapper;

    @InjectMocks
    private MailRequestServiceImpl mailRequestService;

    @BeforeEach
    void setup() {
        mailRequest = MailRequest.builder()
                .toEmail("test@example.com")
                .subject("테스트 메일 제목")
                .body("테스트 메일 본문")
                .build();
    }

    @Test
    @DisplayName("메일 요청 저장 시 READY 상태로 저장된다")
    void createRequest_shouldSaveWithReadyStatus() {
        MailRequestCreateRequestDto dto = new MailRequestCreateRequestDto("test@example.com", "테스트 메일 제목", "테스트 메일 본문");
        when(mailRequestMapper.toEntity(dto)).thenReturn(mailRequest);
        when(mailRequestRepository.save(mailRequest)).thenReturn(mailRequest);

        mailRequestService.createRequest(dto);

        assertThat(mailRequest.getStatus()).isEqualTo(MailStatus.READY);
    }
}
