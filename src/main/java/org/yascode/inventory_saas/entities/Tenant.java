package org.yascode.inventory_saas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(
        name = "tenants",
        indexes = {
                @Index(name = "idx_tenants_tenant_id", columnList = "tenant_id")
        }
)
public class Tenant extends AbstractEntity implements Serializable {

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_code", nullable = false, unique = true)
    private String companyCode;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private TenantStatus status = TenantStatus.PENDING;

    // initial admin credentials
    @Column(name = "admin_full_name", nullable = false)
    private String adminFullName;

    @Column(name = "admin_email", nullable = false, unique = true)
    private String adminEmail;

    @Column(name = "admin_username", nullable = false, unique = true)
    private String adminUsername;

    @Column(name = "admin_password", nullable = false)
    private String adminPassword;

    @Override
    protected String resolveTenantId() {
        return this.getId();
    }
}
