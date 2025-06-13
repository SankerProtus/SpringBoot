package com.codeQuest.Chatterly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class ChatterlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatterlyApplication.class, args);
	}

}
