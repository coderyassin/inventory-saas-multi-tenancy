package org.yascode.inventory_saas.mappers;

import org.springframework.stereotype.Component;
import org.yascode.inventory_saas.entities.Tenant;
import org.yascode.inventory_saas.requests.RegisterTenantRequest;
import org.yascode.inventory_saas.responses.TenantResponse;

import java.time.LocalDateTime;

@Component
public class TenantMapper {

    public Tenant toEntity(final RegisterTenantRequest request) {
        return Tenant.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .createdAt(LocalDateTime.now())
                .email(request.getEmail())
                .adminFullName(request.getAdminFullName())
                .adminEmail(request.getAdminEmail())
                .adminUsername(request.getAdminUsername())
                .build();
    }

    public TenantResponse toResponse(final Tenant tenant) {
        return TenantResponse.builder()
                             .companyName(tenant.getCompanyName())
                             .companyCode(tenant.getCompanyCode())
                             .createdAt(tenant.getCreatedAt())
                             .email(tenant.getEmail())
                             .adminFullName(tenant.getAdminFullName())
                             .adminEmail(tenant.getAdminEmail())
                             .adminUsername(tenant.getAdminUsername())
                .build();
    }
}
