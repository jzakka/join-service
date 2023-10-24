package com.example.joinservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JoinServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoinServiceApplication.class, args);
    }

}
