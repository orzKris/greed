package com.kris.greed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GreedApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreedApplication.class, args);
    }

}
