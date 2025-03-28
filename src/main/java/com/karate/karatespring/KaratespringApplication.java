package com.karate.karatespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KaratespringApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaratespringApplication.class, args);
    }

}
