package com.mink.massac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MassacApplication {

    public static void main(String[] args) {
        SpringApplication.run(MassacApplication.class, args);
    }

}
