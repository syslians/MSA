package com.example.userservice.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class Greeting {
    @Value("${greeting.message}") /* appltcation.yml 파일에 있는 greeting.message를 가져옴. */
    private String message;
}
