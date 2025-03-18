package com.openarena.openarenaportalapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OpenarenaportalapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenarenaportalapiApplication.class, args);
	}

}
