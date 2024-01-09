package com.example.fisrtservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/first-service")
@Slf4j
public class FirstServiceController {

    // 환경 변수(Environment)를 주입받는 필드
    Environment env;

    // 생성자를 통한 환경 변수 주입
    @Autowired
    public FirstServiceController(Environment env) {
        this.env = env;
    }

    // "/first-service/welcome" 엔드포인트에 대한 GET 요청 처리
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First service";
    }

    // "/first-service/message" 엔드포인트에 대한 GET 요청 처리
    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        // 요청 헤더 정보를 로깅
        log.info(header);
        return "Hello World in First Service";
    }

    // "/first-service/check" 엔드포인트에 대한 GET 요청 처리
    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        // 서버 포트 정보를 로깅
        log.info("Server port={}", request.getServerPort());

        // 포트 정보와 환경 변수 값을 포함한 응답 반환
        return String.format("Hi, there. This is a message from First Service in PORT %s"
                , env.getProperty("local.server.port"));
    }
}
