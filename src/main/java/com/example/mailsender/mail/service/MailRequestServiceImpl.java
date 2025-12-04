package com.example.mailsender.mail.service;

import com.example.mailsender.global.config.MailDispatchProperties;
import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.domain.MailStatus;
import com.example.mailsender.mail.dto.MailRequestCreateRequestDto;
import com.example.mailsender.mail.dto.MailRequestResponseDto;
import com.example.mailsender.mail.mapper.MailRequestMapper;
import com.example.mailsender.mail.repository.MailRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailRequestServiceImpl implements MailRequestService {

    private final MailRequestRepository mailRequestRepository;
    private final MailSenderService mailSenderService;
    private final AsyncMailSenderService asyncMailSenderService;
    private final MailRequestMapper mailRequestMapper;
    private final MailDispatchProperties mailDispatchProperties;
    private final MailReservationService mailReservationService;

    @Override
    @Transactional
    public MailRequestResponseDto createRequest(MailRequestCreateRequestDto dto) {
        MailRequest entity = mailRequestMapper.toEntity(dto);
        mailRequestRepository.save(entity);
        return mailRequestMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public MailRequestResponseDto createRequestAndSend(MailRequestCreateRequestDto dto) {
        // 1. 먼저 요청 저장 (status(defalut): READY)
        MailRequest entity = mailRequestMapper.toEntity(dto);
        mailRequestRepository.save(entity);

        // 2. 비동기로 발송 트리거
        asyncMailSenderService.sendMailAsync(entity.getId());

        // 3. 호출 시점에는 아직 SENT/FAILED 아닐 수 있음 (비동기라서) -> 나중 조회시 전송상태 확인 가능
        return mailRequestMapper.toResponseDto(entity);
    }

    @Override
    public MailRequestResponseDto getMailRequest(Long id) {
        MailRequest entity = mailRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MailRequest not found. id=" + id));

        return mailRequestMapper.toResponseDto(entity);
    }

    @Override
    public List<MailRequestResponseDto> search(MailStatus status, LocalDateTime from, LocalDateTime to) {
        List<MailRequest> results = mailRequestRepository.search(status, from, to);

        return results.stream()
                .map(mailRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<MailRequestResponseDto> createBulk(List<MailRequestCreateRequestDto> dtos) {
        List<MailRequest> saved = saveBulk(dtos);
        return saved.stream()
                .map(mailRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<MailRequestResponseDto> createBulkAndSend(List<MailRequestCreateRequestDto> dtos) {
        List<MailRequest> saved = saveBulk(dtos);

        saved.forEach(mail -> asyncMailSenderService.sendMailAsync(mail.getId()));

        return saved.stream()
                .map(mailRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public int dispatchReadyMails() {
        int limit = mailDispatchProperties.getLimit();
        int totalDispatched = 0;

        if (limit <= 0)
            return 0;

        while (true) {
            List<MailRequest> reserveMails = mailReservationService.reserveReadyMails(limit);

            if (reserveMails.isEmpty())
                break;

            for (MailRequest mail : reserveMails) {
                asyncMailSenderService.sendMailAsync(mail.getId());
            }

            totalDispatched += reserveMails.size();
        }

        if (totalDispatched > 0)
            log.info("[DISPATCH] READY {}건에 대해 비동기 발송을 트리거했습니다.", totalDispatched);
        else
            log.info("[DISPATCH] READY 상태 메일이 없습니다.");

        return totalDispatched;
    }

    @Transactional
    protected List<MailRequest> saveBulk(List<MailRequestCreateRequestDto> dtos) {
        List<MailRequest> entities = dtos.stream()
                .map(mailRequestMapper::toEntity)
                .toList();

        return mailRequestRepository.saveAll(entities);
    }
}
