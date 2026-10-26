package org.yascode.inventory_saas.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.inventory_saas.common.PageResponse;
import org.yascode.inventory_saas.entities.Tenant;
import org.yascode.inventory_saas.entities.TenantStatus;
import org.yascode.inventory_saas.entities.User;
import org.yascode.inventory_saas.entities.UserRole;
import org.yascode.inventory_saas.exceptions.DuplicateResourceException;
import org.yascode.inventory_saas.exceptions.InvalidRequestException;
import org.yascode.inventory_saas.mappers.TenantMapper;
import org.yascode.inventory_saas.repositories.TenantRepository;
import org.yascode.inventory_saas.repositories.UserRepository;
import org.yascode.inventory_saas.requests.RegisterTenantRequest;
import org.yascode.inventory_saas.responses.TenantResponse;
import org.yascode.inventory_saas.services.TenantService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void registerTenant(final RegisterTenantRequest request) {
        // check if the tenant already exists by company code
        if (this.tenantRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new DuplicateResourceException("Tenant already exists");
        }

        // check if email already exits
        if (this.tenantRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Tenant Email already exists");
        }

        // create tenant entity
        final Tenant tenant = this.tenantMapper.toEntity(request);
        tenant.setAdminPassword(this.passwordEncoder.encode(request.getAdminPassword()));
        tenant.setStatus(TenantStatus.PENDING);

        this.tenantRepository.save(tenant);

    }

    @Override
    public void approveTenant(final String tenantId) {
        // check if tenant exists
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                                                   .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        // activate tenant
        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);

        try {
            // create initial admin user
            createInitiaAdminUser(tenant);
        } catch (final Exception e) {
            rollbackTenantStatus(tenant);
        }

    }

    @Override
    public void activateTenant(final String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                                                   .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new InvalidRequestException("Tenant is not pending");
        }

        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void deactivateTenant(final String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                                                   .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant is not pending");
        }

        tenant.setStatus(TenantStatus.INACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void suspendTenant(final String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                                                   .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant is not pending");
        }

        tenant.setStatus(TenantStatus.SUSPENDED);
        this.tenantRepository.save(tenant);
    }

    @Override
    public PageResponse<TenantResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Tenant> tenants = this.tenantRepository.findAll(pageRequest);
        final Page<TenantResponse> tenantResponses = tenants.map(this.tenantMapper::toResponse);
        return PageResponse.of(tenantResponses);
    }

    private void rollbackTenantStatus(final Tenant tenant) {
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);
    }

    private void createInitiaAdminUser(final Tenant tenant) {
        // check if the user already exists
        if (this.userRepository.existsByUsername(tenant.getAdminUsername())) {
            throw new DuplicateResourceException("User already exists");
        }

        final User adminUser = User.builder()
                                   .username(tenant.getAdminUsername())
                                   .email(tenant.getAdminEmail())
                                   .firstName(extractFirstName(tenant.getAdminFullName()))
                                   .lastName(extractLastName(tenant.getAdminFullName()))
                                   .password(tenant.getAdminPassword())
                                   .role(UserRole.ROLE_COMPANY_ADMIN)
                                   .tenant(tenant)
                                   .tenantId(tenant.getId())
                                   .enabled(true)
                                   .build();
        this.userRepository.save(adminUser);
        log.info("Created initial admin user for tenant {}", tenant.getId());
    }

    private String extractFirstName(final String fullName) {
        return fullName.split(" ")[0];
    }

    private String extractLastName(final String fullName) {
        return fullName.split(" ").length > 1 ? fullName.split(" ")[1] : fullName;
    }
}
