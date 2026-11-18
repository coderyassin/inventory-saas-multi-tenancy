package org.yascode.inventory_saas.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yascode.inventory_saas.common.PageResponse;
import org.yascode.inventory_saas.config.TenantContext;
import org.yascode.inventory_saas.entities.Tenant;
import org.yascode.inventory_saas.entities.User;
import org.yascode.inventory_saas.entities.UserRole;
import org.yascode.inventory_saas.event.UserCreatedEvent;
import org.yascode.inventory_saas.exceptions.DuplicateResourceException;
import org.yascode.inventory_saas.exceptions.InvalidRequestException;
import org.yascode.inventory_saas.mappers.UserMapper;
import org.yascode.inventory_saas.repositories.UserRepository;
import org.yascode.inventory_saas.requests.UserRequest;
import org.yascode.inventory_saas.responses.UserResponse;
import org.yascode.inventory_saas.services.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final String USER_DOES_NOT_EXIST = "User does not exist";

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createUser(final UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Creating user for tenant: {}", tenantId);

        // validate if username exists
        if (this.repository.existsByUsername(request.getUsername())) {
            log.warn("User creation failed: username already exists for tenant: {}", tenantId);
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email exists
        if (this.repository.existsByEmail(request.getEmail())) {
            log.warn("User creation failed: email already exists for tenant: {}", tenantId);
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM_ADMIN)
        if (request.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            log.warn("User creation failed: invalid role for tenant: {}", tenantId);
            throw new InvalidRequestException("Role is required");
        }

        // encode password
        final User user = this.userMapper.toEntity(request);
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user.setTenant(Tenant.builder().id(tenantId).build());

        this.repository.save(user);

        log.info("User created successfully");

        log.info("Publishing user created event for tenant: {}", tenantId);
        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), user.getEmail(), user.getCreatedAt()));
        log.info("User created event published successfully for tenant: {}", tenantId);
    }

    @Override
    @Transactional
    public void updateUser(final String userId, final UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Updating user for tenant: {}", tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> {
                    log.warn("User update failed: user not found with id: {}", userId);
                    return new EntityNotFoundException(USER_DOES_NOT_EXIST);
                });

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            log.warn("User update failed: user {} does not belong to tenant: {}", userId, tenantId);
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // check if username is being changed and if it is already taken
        if (!user.getUsername().equals(request.getUsername()) && this.repository.existsByUsername(request.getUsername())) {
            log.warn("User update failed: username already exists for tenant: {}", tenantId);
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email is being changed and if it is already taken
        if (!user.getEmail().equals(request.getEmail()) && this.repository.existsByEmail(request.getEmail())) {
            log.warn("User update failed: email already exists for tenant: {}", tenantId);
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM_ADMIN)
        if (request.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            log.warn("User update failed: invalid role for user: {} tenant: {}", userId, tenantId);
            throw new InvalidRequestException("Role is required");
        }

        // update user details
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        this.repository.save(user);
        log.info("User updated successfully");
    }

    @Override
    @Transactional
    public void deleteUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Deleting user for tenant: {}", tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> {
                    log.warn("User deletion failed: user not found with id: {}", userId);
                    return new EntityNotFoundException(USER_DOES_NOT_EXIST);
                });

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            log.warn("User deletion failed: user {} does not belong to tenant: {}", userId, tenantId);
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // soft delete user
        user.setDeleted(true);
        this.repository.save(user);
        log.info("User deleted successfully: {} for tenant: {}", userId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Fetching user: {} for tenant: {}", userId, tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> {
                    log.warn("Fetch user failed: user not found with id: {}", userId);
                    return new EntityNotFoundException(USER_DOES_NOT_EXIST);
                });

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            log.warn("Fetch user failed: user {} does not belong to tenant: {}", userId, tenantId);
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        log.info("User fetched successfully: {} for tenant: {}", userId, tenantId);
        return this.userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(final int page, final int size) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Fetching users for tenant: {}, page: {}, size: {}", tenantId, page, size);

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<User> userPage = this.repository.findAllByTenantId(tenantId, pageRequest);
        final Page<UserResponse> userResponses = userPage.map(this.userMapper::toResponse);

        log.info("Fetched {} users for tenant: {}", userPage.getNumberOfElements(), tenantId);
        return PageResponse.of(userResponses);
    }

    @Override
    @Transactional
    public void enableUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Enabling user: {} for tenant: {}", userId, tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> {
                    log.warn("Enable user failed: user not found with id: {}", userId);
                    return new EntityNotFoundException(USER_DOES_NOT_EXIST);
                });

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            log.warn("Enable user failed: user {} does not belong to tenant: {}", userId, tenantId);
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setEnabled(true);
        this.repository.save(user);
        log.info("User enabled successfully: {} for tenant: {}", userId, tenantId);
    }

    @Override
    @Transactional
    public void disableUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Disabling user: {} for tenant: {}", userId, tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> {
                    log.warn("Disable user failed: user not found with id: {}", userId);
                    return new EntityNotFoundException(USER_DOES_NOT_EXIST);
                });

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            log.warn("Disable user failed: user {} does not belong to tenant: {}", userId, tenantId);
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setEnabled(false);
        this.repository.save(user);
        log.info("User disabled successfully: {} for tenant: {}", userId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.info("Loading user by username");

        return this.repository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Load user failed: no user found with username");
                    return new UsernameNotFoundException("No user was found with: " + username);
                });
    }
}
