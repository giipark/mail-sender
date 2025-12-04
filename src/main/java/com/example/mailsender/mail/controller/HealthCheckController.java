package com.example.mailsender.mail.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "서버 상태 체크", description = "서버 상태를 체크하기 위한 API")
@RestController
public class HealthCheckController {

    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}
