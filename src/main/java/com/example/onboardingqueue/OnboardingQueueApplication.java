package com.example.onboardingqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnboardingQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnboardingQueueApplication.class, args);
    }
}
