package org.yascode.inventory_saas.responses;

import lombok.*;
import org.yascode.inventory_saas.entities.TenantStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantResponse {

    private String tenantId;
    private String companyName;
    private String companyCode;
    private String email;
    private String adminFullName;
    private String adminEmail;
    private String adminUsername;
    private String adminPassword;
    private LocalDateTime createdAt;
    private TenantStatus status;
}
