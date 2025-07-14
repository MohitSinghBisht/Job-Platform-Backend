package com.jobhunt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.jobhunt.model")
@EnableJpaRepositories("com.jobhunt.repository")
public class JobHuntApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobHuntApplication.class, args);
    }
}
