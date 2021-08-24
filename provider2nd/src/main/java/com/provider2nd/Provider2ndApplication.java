package com.provider2nd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Provider2ndApplication {
    public static void main(String[] args) {
        new SpringApplication().run(Provider2ndApplication.class, args);
    }
}
