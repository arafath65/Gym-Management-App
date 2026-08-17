package com.arafath65.gymManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GymManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				GymManagementApplication.class,
				args
		);
	}
}