package com.spring.dog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
	    scanBasePackages = "com.spring",
	    exclude = {
	        org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.class
	    }
	)
@EnableJpaRepositories(basePackages = {"com.spring.repository"})
@EntityScan(basePackages = {"com.spring.domain"})
@ComponentScan(basePackages = {"com.spring"})
@EnableScheduling

public class DogApplication {
    public static void main(String[] args) {
        SpringApplication.run(DogApplication.class, args);
    }

}
