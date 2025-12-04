package com.example.mailsender.mail.mapper;

import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.dto.MailRequestCreateRequestDto;
import com.example.mailsender.mail.dto.MailRequestResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MailRequestMapper {

    MailRequest toEntity(MailRequestCreateRequestDto dto);

    MailRequestResponseDto toResponseDto(MailRequest entity);
}
