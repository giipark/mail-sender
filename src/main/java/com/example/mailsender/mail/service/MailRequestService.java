package com.example.mailsender.mail.service;

import com.example.mailsender.mail.domain.MailStatus;
import com.example.mailsender.mail.dto.MailRequestCreateRequestDto;
import com.example.mailsender.mail.dto.MailRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface MailRequestService {

    MailRequestResponseDto createRequest(MailRequestCreateRequestDto dto);

    MailRequestResponseDto createRequestAndSend(MailRequestCreateRequestDto dto);

    MailRequestResponseDto getMailRequest(Long id);

    List<MailRequestResponseDto> search(MailStatus status, LocalDateTime from, LocalDateTime to);

    List<MailRequestResponseDto> createBulk(List<MailRequestCreateRequestDto> dtos);

    List<MailRequestResponseDto> createBulkAndSend(List<MailRequestCreateRequestDto> dtos);

    int dispatchReadyMails();
}

