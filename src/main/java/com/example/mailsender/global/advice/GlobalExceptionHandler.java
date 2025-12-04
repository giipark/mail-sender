package com.example.mailsender.global.advice;

import com.example.mailsender.global.exception.CustomException;
import com.example.mailsender.global.exception.ErrorCode;
import com.example.mailsender.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            CustomException e, HttpServletRequest request) {

        ErrorCode code = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(code, request.getRequestURI());

        log.warn("[CustomException] {} - {}", code, e.getMessage());
        return ResponseEntity.status(code.getStatus()).body(response);
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "유효성 검사 실패";

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 그 외 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {

        log.error("[UnhandledException] {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다.",
                request.getRequestURI()
        );

        return ResponseEntity.internalServerError().body(response);
    }
}
