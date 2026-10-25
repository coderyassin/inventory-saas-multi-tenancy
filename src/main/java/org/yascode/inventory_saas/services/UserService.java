package org.yascode.inventory_saas.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.yascode.inventory_saas.common.PageResponse;
import org.yascode.inventory_saas.requests.UserRequest;
import org.yascode.inventory_saas.responses.UserResponse;

public interface UserService extends UserDetailsService {

    void createUser(final UserRequest request);

    void updateUser(final String userId, final UserRequest request);

    void deleteUser(final String userId);

    UserResponse getUserById(final String userId);

    PageResponse<UserResponse> getAllUsers(final int page, final int size);

    void enableUser(final String userId);

    void disableUser(final String userId);
}
