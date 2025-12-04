package com.example.mailsender.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mail.dispatch")
public class MailDispatchProperties {

    private int limit = 10;
}
