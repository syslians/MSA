package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

//    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/first-service/**") /* 사용자로부터 first-service로 요청이 들어옵니다. */
                        .filters(f -> f.addRequestHeader("first-request", "first-request-header") /* 요청 filter에는 해당 헤더를 추가하고 */
                                       .addResponseHeader("first-response", "first-response-header")) /* 응답 filter에는 해당 헤더를 추가. */
                        .uri("http://localhost:8081")) /* 위 과정을 거쳐 이 주소로 라우팅. */
                .route(r -> r.path("/second-service/**") /* 사용자로부터 first-service로 요청이 들어옵니다. */
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header") /* 요청 filter에는 해당 헤더를 추가하고 */
                                .addResponseHeader("second-response", "second-response-header")) /* 응답 filter에는 해당 헤더를 추가. */
                        .uri("http://localhost:8082")) /* 위 과정을 거쳐 이 주소로 라우팅. */
                .build();
    }
}
