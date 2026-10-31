package org.yascode.inventory_saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class InventorySaasApplication {

	static void main(String[] args) {
		SpringApplication.run(InventorySaasApplication.class, args);
	}
}
