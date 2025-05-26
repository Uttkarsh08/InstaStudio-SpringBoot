package com.uttkarsh.InstaStudio;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
		info = @Info(
				title = "InstaStudio API",
				version = "1.0",
				description = "API documentation for InstaStudio backend"
		)
)
@SpringBootApplication
@EnableScheduling
public class InstaStudioApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstaStudioApplication.class, args);
	}

}
