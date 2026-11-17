package com.sanem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SanemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanemApplication.class, args);
    }
}
