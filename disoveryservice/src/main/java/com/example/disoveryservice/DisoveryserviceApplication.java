package com.example.disoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DisoveryserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisoveryserviceApplication.class, args);
    }

}
