package com.environmental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnvironmentMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvironmentMonitoringApplication.class, args);
	}

}
