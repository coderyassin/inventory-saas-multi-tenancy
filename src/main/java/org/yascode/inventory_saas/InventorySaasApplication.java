package org.yascode.inventory_saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yascode.inventory_saas.entities.Tenant;
import org.yascode.inventory_saas.entities.User;
import org.yascode.inventory_saas.repositories.TenantRepository;
import org.yascode.inventory_saas.repositories.UserRepository;

import static org.yascode.inventory_saas.entities.TenantStatus.ACTIVE;
import static org.yascode.inventory_saas.entities.UserRole.ROLE_ADMINISTRATOR;
import static org.yascode.inventory_saas.entities.UserRole.ROLE_PLATFORM_ADMIN;

@SpringBootApplication
@EnableJpaAuditing
public class InventorySaasApplication {

	static void main(String[] args) {
		SpringApplication.run(InventorySaasApplication.class, args);
	}

	//@Bean
	int createUser(UserRepository userRepository, TenantRepository tenantRepository, PasswordEncoder passwordEncoder) {

		Tenant tenant = Tenant.builder()
				.companyCode("INT")
				.companyName("INT")
				.createdBy("SYSTEM")
				.email("INT")
				.adminUsername("INT")
				.adminEmail("INT")
				.adminFullName("INT")
				.adminPassword(passwordEncoder.encode("INT"))
				.status(ACTIVE)
				.build();

		tenant = tenantRepository.saveAndFlush(tenant);

		User user = User.builder()
				.username("admin")
				.password(passwordEncoder.encode("admin"))
				.role(ROLE_PLATFORM_ADMIN)
				.firstName("Admin")
				.lastName("Admin")
				.email("admin@admin.com")
				.tenantId(tenant.getTenantId())
				.build();

		userRepository.save(user);

		return 0;
	}



}
