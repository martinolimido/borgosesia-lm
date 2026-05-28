package it.borgosesiaspa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = "it.borgosesiaspa")
public class BGSLeaseManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(BGSLeaseManagementApplication.class, args);
	}

}
