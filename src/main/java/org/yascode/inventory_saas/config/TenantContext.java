package org.yascode.inventory_saas.config;

/**
 * TenantContext - Stores the current tenant identifier and schema in a ThreadLocal.
 *
 * Each HTTP request is handled by a dedicated thread.
 * The ThreadLocal guarantees that the tenant_id and schema are isolated per thread,
 * even when requests from different tenants arrive simultaneously.
 *
 * Flow:
 *    1. JwtAuthenticationFilter extracts the tenant_id from the HTTP request
 *    2. JwtAuthenticationFilter calls TenantContext.setCurrentTenant(tenant_id) and TenantContext.setCurrentSchema(tenant_id)
 *    3. Business code (services, repositories) accesses the tenant and schema via TenantContext.getCurrentTenant() and TenantContext.getCurrentSchema()
 *    4. JwtAuthenticationFilter calls TenantContext.clear() after the response (cleanup)
 *
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_SCHEMA = new ThreadLocal<>();


    /**
     * Sets the tenant identifier for the current thread.
     */
    public static void setCurrentTenant(final String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    /**
     * Sets the schema identifier for the current thread.
     */
    public static void setCurrentSchema(final String schema) {
        CURRENT_SCHEMA.set(schema);
    }

    /**
     * Retrieves the tenant identifier for the current thread.
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    /**
     * Retrieves the schema identifier for the current thread.
     */
    public static String getCurrentSchema() {
        return CURRENT_SCHEMA.get();
    }

    /**
     * Clears the tenant from the current thread.
     * Clears the schema from the current thread.
     * IMPORTANT: must be called in a finally block
     * to prevent memory leaks
     * and data leaking between HTTP requests.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_SCHEMA.remove();
    }
}
