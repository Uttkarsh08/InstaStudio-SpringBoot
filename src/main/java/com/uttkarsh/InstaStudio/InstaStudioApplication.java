package com.uttkarsh.InstaStudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InstaStudioApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstaStudioApplication.class, args);
	}

}
