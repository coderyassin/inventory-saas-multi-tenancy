package org.yascode.inventory_saas.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * TenantHibernateFilter — Automatically enables the Hibernate filter
 * before every call to Spring Data JPA repositories.
 *
 * HOW IT WORKS:
 *   1. TenantFilter (HTTP) has already stored the tenantId in TenantContext
 *   2. This aspect intercepts every call to a Repository
 *   3. It enables the Hibernate "tenantFilter" with the current tenantId
 *   4. Hibernate automatically adds WHERE tenant_id = :tenantId
 *
 * WHY AN ASPECT?
 *   Without this aspect, the filter would have to be enabled manually in each
 *   service method. The aspect does it automatically and in a cross-cutting way.
 *
 * ALTERNATIVE:
 *   A HandlerInterceptor or an @EventListener could also be used.
 *   The aspect is cleaner because it runs closest to the data layer.
 */

@Aspect
@Component
public class TenantHibernateFilter {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Intercepts all calls to the service methods
     * in the package org.yascode.inventory_saas.services
     *
     * execution(* org.yascode.inventory_saas.services.*.*(..))
     *   → any method (*), of any class (*), with any arguments (..)
     *   → in the package org.yascode.inventory_saas.services
     */
    @Before("execution(* org.yascode.inventory_saas.services.*.*(..))")
    public void activateTenantFilter() {
        final String tenantId = TenantContext.getCurrentTenant();

        if (tenantId != null) {
            final Session session = this.entityManager.unwrap(Session.class);

            // enable the filter and inject the tenantId parameter
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId);
        }
    }
}
