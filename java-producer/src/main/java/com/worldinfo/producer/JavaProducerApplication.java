package com.worldinfo.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavaProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaProducerApplication.class, args);
    }
}
