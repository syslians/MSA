package com.example.userservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {

    // Resilience4J CircuitBreakerFactory에 대한 커스터마이저 빈을 생성하는 메서드
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {

        // Circuit Breaker의 설정을 정의하는 CircuitBreakerConfig 생성
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4) // 4번의 호출 중에 50% 이상이 실패하면 Circuit Breaker 동작
                .waitDurationInOpenState(Duration.ofMillis(1000)) // Open 상태에서 Half-Open으로 전환되는 대기 시간 (1초)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // 호출 횟수 기반의 윈도우 타입 사용
                .slidingWindowSize(2) // 윈도우의 크기 (최근 2개의 호출 기록을 보고 실패율 계산)
                .build();

        // TimeLimiter의 설정을 정의하는 TimeLimiterConfig 생성
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4)) // 호출에 대한 최대 타임아웃 (4초)
                .build();

        // Resilience4J CircuitBreakerFactory를 커스터마이징하는 Customizer 반환
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig) // TimeLimiter 설정 적용
                .circuitBreakerConfig(circuitBreakerConfig) // CircuitBreaker 설정 적용
                .build()
        );
    }
}
