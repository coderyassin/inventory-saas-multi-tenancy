package org.yascode.inventory_saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yascode.inventory_saas.entities.User;
import org.yascode.inventory_saas.repositories.UserRepository;

import static org.yascode.inventory_saas.entities.UserRole.ROLE_ADMINISTRATOR;

@SpringBootApplication
@EnableJpaAuditing
public class InventorySaasApplication {

	static void main(String[] args) {
		SpringApplication.run(InventorySaasApplication.class, args);
	}

	//@Bean
	int createUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {

		User user = User.builder()
				.username("admin")
				.password(passwordEncoder.encode("admin"))
				.role(ROLE_ADMINISTRATOR)
				.firstName("Admin")
				.lastName("Admin")
				.email("admin@admin.com")
				.tenantId("yascode")
				.build();

		userRepository.save(user);

		return 0;
	}



}
