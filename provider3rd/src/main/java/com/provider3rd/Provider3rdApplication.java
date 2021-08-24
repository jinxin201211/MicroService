package com.provider3rd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Provider3rdApplication {
    public static void main(String[] args) {
        new SpringApplication().run(Provider3rdApplication.class, args);
    }
}
