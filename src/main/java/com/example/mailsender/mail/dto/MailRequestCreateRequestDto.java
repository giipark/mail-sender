package com.example.mailsender.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record   MailRequestCreateRequestDto(
        @Schema(description = "수신자 이메일", example = "gipark@nineonesoft.com")
        @NotBlank @Email
        String toEmail,

        @Schema(description = "메일 제목", example = "테스트 메일 제목")
        @NotBlank
        String subject,

        @Schema(description = "메일 본문(HTML 지원)", example = "<h1>테스트 메일</h1><p>테스트 메일 본문</p>")
        @NotBlank
        String body
) {
}
