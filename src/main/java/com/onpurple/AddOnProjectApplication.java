package com.onpurple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AddOnProjectApplication {
	public static void main(String[] args) {
		SpringApplication.run(AddOnProjectApplication.class, args);
	}

}
