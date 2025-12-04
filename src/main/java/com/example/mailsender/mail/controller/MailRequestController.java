package com.example.mailsender.mail.controller;

import com.example.mailsender.mail.domain.MailStatus;
import com.example.mailsender.mail.dto.MailDispatchResponseDto;
import com.example.mailsender.mail.dto.MailRequestCreateRequestDto;
import com.example.mailsender.mail.dto.MailRequestResponseDto;
import com.example.mailsender.mail.mapper.MailRequestMapper;
import com.example.mailsender.mail.service.MailRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "메일 발송 관리", description = "메일 생성, 조회 및 발송 관련 API")
@RestController
@RequestMapping("/api/mails")
@RequiredArgsConstructor
public class MailRequestController {

    private final MailRequestService mailRequestService;
    private final MailRequestMapper mailRequestMapper;

    /**
     * 메일 발송 요청만 생성 (READY 상태로 저장)
     *
     * @param request
     * @return
     */
    @Operation(summary = "메일 발송 요청 생성", description = "메일 데이터를 DB에 저장하고 READY 상태로 생성합니다.")
    @PostMapping
    public ResponseEntity<MailRequestResponseDto> createMailRequest(
            @Valid @RequestBody MailRequestCreateRequestDto request
    ) {
        MailRequestResponseDto response = mailRequestService.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 메일 발송 요청 생성 + 즉시 발송
     *
     * @param request
     * @return
     */
    @PostMapping("/send")
    public ResponseEntity<MailRequestResponseDto> createAndSendMail(
            @Valid @RequestBody MailRequestCreateRequestDto request
    ) {
        MailRequestResponseDto response = mailRequestService.createRequestAndSend(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 메일 요청 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<MailRequestResponseDto> getMailRequest(@PathVariable Long id) {
        MailRequestResponseDto response = mailRequestService.getMailRequest(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 상태/생성일 기준 간단 조건 조회
     * 예)
     * GET /api/mails?status=READY
     * GET /api/mails?status=SENT&from=2025-12-01T00:00:00&to=2025-12-03T23:59:59
     *
     * @param status
     * @param from
     * @param to
     * @return
     */
    @GetMapping
    public ResponseEntity<List<MailRequestResponseDto>> searchMailRequests(
            @RequestParam(required = false) MailStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        List<MailRequestResponseDto> responseDtoList = mailRequestService.search(status, from, to);

        return ResponseEntity.ok(responseDtoList);
    }

    @Operation(summary = "메일 요청 bulk 생성", description = "여러 건의 메일 요청을 한 번에 생성합니다.")
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MailRequestResponseDto> createBulk(
            @RequestBody List<@Valid MailRequestCreateRequestDto> dtos,
            @RequestParam(name = "sendImmediately", defaultValue = "false") boolean sendImmediately
    ) {
        if (sendImmediately)
            return mailRequestService.createBulkAndSend(dtos);
        return mailRequestService.createBulk(dtos);
    }

    @Operation(summary = "READY 상태 메일 발송 디스패치", description = "READY 상태 메일을 오래된 순으로 최대 limit 개수만큼 비동기 발송합니다.")
    @PostMapping("/dispatch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MailDispatchResponseDto dispatchReadyMails() {
        int dispatchedCount = mailRequestService.dispatchReadyMails();
        return new MailDispatchResponseDto(dispatchedCount);
    }
}
