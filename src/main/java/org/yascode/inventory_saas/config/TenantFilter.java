package org.yascode.inventory_saas.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Tenant - Intercepts EVERY HTTP request to identify the tenant.
 *
 * This filter is the entry point of the multi-tenant mechanism.
 * It runs before all controllers and services.
 *
 * Tenant identification strategy (in order of priority):
 *  1. "X-Tenant-ID" header
 *  2. (Optional) Subdomain: alpha.stockapp.com -> "alpha"
 *
 * If no tenant is identified -> 400 BAD REQUEST response
 */


//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final String tenantId = resolveTenant(request);
        if (tenantId == null || tenantId.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\": \"Tenant ID is missing in the request header, please add the header X-Tenant-ID\"}"
            );
            return;
        }
        try {
            // store the tenant in the ThreadLocal
            TenantContext.setCurrentTenant(tenantId);

            // continue the filter chain -> controller -> service
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // CRITICAL: always clear the ThreadLocal after the request
            // Without this clear(), the tenant could "leak" into the next request
            // if the thread is reused by the server's thread pool
            TenantContext.clear();
        }
    }

    private String resolveTenant(final HttpServletRequest request) {
        final String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId != null && !tenantId.isBlank()) {
            return tenantId.trim().toLowerCase();
        }
        return null;
    }
}
