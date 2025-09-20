package com.realestate.real_estate_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealEstatePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealEstatePlatformApplication.class, args);
	}

}
