package org.yascode.inventory_saas.services;

import org.yascode.inventory_saas.common.PageResponse;
import org.yascode.inventory_saas.requests.RegisterTenantRequest;
import org.yascode.inventory_saas.responses.TenantResponse;

public interface TenantService {

    void registerTenant(final RegisterTenantRequest request);

    void approveTenant(final String tenantId);

    void activateTenant(final String tenantId);

    void deactivateTenant(final String tenantId);

    void suspendTenant(final String tenantId);

    PageResponse<TenantResponse> findAll(final int page, final int size);
}
