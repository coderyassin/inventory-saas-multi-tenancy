package org.yascode.inventory_saas.services;

import org.yascode.inventory_saas.entities.Tenant;

public interface ProvisioningService {

    void provisionTenant(final Tenant tenant);
}
