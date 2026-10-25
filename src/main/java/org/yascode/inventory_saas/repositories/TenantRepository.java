package org.yascode.inventory_saas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yascode.inventory_saas.entities.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, String> {

    boolean existsByCompanyCode(String companyCode);

    boolean existsByEmail(String email);
}
