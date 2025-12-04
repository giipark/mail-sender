package com.example.mailsender.global.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ErrorResponse of(ErrorCode code, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                code.getStatus().value(),
                code.getStatus().name(),
                code.getMessage(),
                path
        );
    }

    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                message,
                path
        );
    }
}
