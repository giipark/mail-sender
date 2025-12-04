package com.example.mailsender.mail.controller;

import com.example.mailsender.global.advice.GlobalExceptionHandler;
import com.example.mailsender.mail.domain.MailStatus;
import com.example.mailsender.mail.dto.MailRequestCreateRequestDto;
import com.example.mailsender.mail.dto.MailRequestResponseDto;
import com.example.mailsender.mail.mapper.MailRequestMapper;
import com.example.mailsender.mail.service.MailRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailRequestController.class)
@Import(GlobalExceptionHandler.class)
class MailRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MailRequestService mailRequestService;

    @MockitoBean
    private MailRequestMapper mailRequestMapper;

    private MailRequestResponseDto createSampleResponse(Long id) {
        return new MailRequestResponseDto(
                id,
                "test@example.com",
                "테스트 제목",
                "테스트 본문",
                MailStatus.READY,
                0,
                LocalDateTime.of(2025, 12, 2, 10, 0),
                LocalDateTime.of(2025, 12, 2, 10, 0),
                null,
                null
        );
    }

    @Test
    @DisplayName("POST /api/mails - 메일 발송 요청만 생성(READY 상태)")
    void createMailRequest_shouldReturnCreatedMailRequest() throws Exception {
        // given
        MailRequestCreateRequestDto requestDto = new MailRequestCreateRequestDto(
                "test@example.com",
                "테스트 제목",
                "테스트 본문"
        );
        MailRequestResponseDto responseDto = createSampleResponse(1L);

        given(mailRequestService.createRequest(any(MailRequestCreateRequestDto.class)))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/mails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.toEmail").value("test@example.com"))
                .andExpect(jsonPath("$.subject").value("테스트 제목"))
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    @DisplayName("POST /api/mails/send - 메일 요청 생성 후 즉시 발송")
    void createRequestAndSend_shouldReturnCreatedMailRequest() throws Exception {
        // given
        MailRequestCreateRequestDto requestDto = new MailRequestCreateRequestDto(
                "test@example.com",
                "테스트 제목",
                "테스트 본문"
        );

        MailRequestResponseDto responseDto = createSampleResponse(2L);

        given(mailRequestService.createRequestAndSend(any(MailRequestCreateRequestDto.class)))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/mails/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.toEmail").value("test@example.com"))
                .andExpect(jsonPath("$.subject").value("테스트 제목"));
    }

    @Test
    @DisplayName("GET /api/mails/{id} - 메일 요청 단건 조회")
    void getMailRequest_shouldReturnMailRequest() throws Exception {
        // given
        MailRequestResponseDto responseDto = createSampleResponse(1L);
        given(mailRequestService.getMailRequest(1L)).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/mails/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.toEmail").value("test@example.com"))
                .andExpect(jsonPath("$.subject").value("테스트 제목"));
    }

    @Test
    @DisplayName("GET /api/mails - 상태와 기간으로 검색")
    void searchMailRequests_shouldReturnList() throws Exception {
        // given
        MailRequestResponseDto r1 = createSampleResponse(1L);
        MailRequestResponseDto r2 = createSampleResponse(2L);

        given(mailRequestService.search(
                any(MailStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(List.of(r1, r2));

        // when & then
        mockMvc.perform(get("/api/mails")
                        .param("status", "READY")
                        .param("from", "2025-12-01T00:00:00")
                        .param("to", "2025-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("요청 DTO 유효성 검증 실패 시 400을 반환한다")
    void createMailRequest_validationFail_shouldReturnBadRequest() throws Exception {
        // given: 잘못된 요청 (email 없음)
        MailRequestCreateRequestDto invalidRequest = new MailRequestCreateRequestDto(
                "",
                "제목",
                "본문"
        );

        // when & then
        mockMvc.perform(post("/api/mails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
