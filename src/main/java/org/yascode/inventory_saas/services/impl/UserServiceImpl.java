package org.yascode.inventory_saas.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yascode.inventory_saas.common.PageResponse;
import org.yascode.inventory_saas.config.TenantContext;
import org.yascode.inventory_saas.entities.User;
import org.yascode.inventory_saas.entities.UserRole;
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
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(final UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Creating user for tenant: {}", tenantId);

        // validate if username exists
        if (this.repository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email exists
        if (this.repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM_ADMIN)
        if (request.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            throw new InvalidRequestException("Role is required");
        }

        final User user = this.userMapper.toEntity(request);

        this.repository.save(user);

        log.info("User created successfully");
    }

    @Override
    public void updateUser(final String userId, final UserRequest request) {
        // TODO implement updateUser
    }

    @Override
    public void deleteUser(final String userId) {
        // TODO implement deleteUser
    }

    @Override
    public UserResponse getUserById(final String userId) {
        // TODO implement getUserById
        return null;
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(final int page, final int size) {
        // TODO implement getAllUsers
        return null;
    }

    @Override
    public void enableUser(final String userId) {
        // TODO implement enableUser
    }

    @Override
    public void disableUser(final String userId) {
        // TODO implement disableUser
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        return this.repository.findByUsername(username)
                              .orElseThrow(() -> new UsernameNotFoundException("No user was found with: " + username));
    }
}
