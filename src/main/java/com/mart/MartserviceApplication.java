package com.mart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling 
public class MartserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MartserviceApplication.class, args);
	}




}
