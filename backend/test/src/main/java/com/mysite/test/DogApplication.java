package com.mysite.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
	    scanBasePackages = {"com.mysite.test"},
	    exclude = {
	        org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.class
	    }
	)
@EnableJpaRepositories(basePackages = {"com.mysite.test"})
@EntityScan(basePackages = {"com.mysite.test"})
@ComponentScan(basePackages = {"com.mysite.test"})
@EnableScheduling

public class DogApplication {
    public static void main(String[] args) {
        SpringApplication.run(DogApplication.class, args);
    }

}
