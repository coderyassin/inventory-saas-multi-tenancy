package org.yascode.inventory_saas.config;

/**
 * TenantContext - Stores the current tenant identifier in a ThreadLocal.
 *
 * Each HTTP request is handled by a dedicated thread.
 * The ThreadLocal guarantees that the tenant_id is isolated per thread,
 * even when requests from different tenants arrive simultaneously.
 *
 * Flow:
 *    1. TenantFilter extracts the tenant_id from the HTTP request
 *    2. TenantFilter calls TenantContext.setCurrentTenant(tenant_id)
 *    3. Business code (services, repositories) accesses the tenant via TenantContext.getCurrentTenant()
 *    4. TenantFilter calls TenantContext.clear() after the response (cleanup)
 *
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();


    /**
     * Sets the tenant identifier for the current thread.
     */
    public static void setCurrentTenant(final String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    /**
     * Retrieves the tenant identifier for the current thread.
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    /**
     * Clears the tenant from the current thread.
     * IMPORTANT: must be called in a finally block
     * to prevent memory leaks
     * and data leaking between HTTP requests.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
