package org.yascode.inventory_saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
public class InventorySaasApplication {

	static void main(String[] args) {
		SpringApplication.run(InventorySaasApplication.class, args);
	}
}
