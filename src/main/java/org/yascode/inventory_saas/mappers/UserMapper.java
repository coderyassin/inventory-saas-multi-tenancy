package org.yascode.inventory_saas.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yascode.inventory_saas.entities.User;
import org.yascode.inventory_saas.requests.UserRequest;
import org.yascode.inventory_saas.responses.UserResponse;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public User toEntity(final UserRequest request) {
        return User.builder()
                   .username(request.getUsername())
                   .email(request.getEmail())
                   .password(request.getPassword())
                   .role(request.getRole())
                   .firstName(request.getFirstName())
                   .lastName(request.getLastName())
                   .build();
    }

    public UserResponse toResponse(final User user) {
        return UserResponse.builder()
                   .username(user.getUsername())
                   .email(user.getEmail())
                   .password(user.getPassword())
                   .role(user.getRole())
                   .firstName(user.getFirstName())
                   .lastName(user.getLastName())
                   .build();
    }
}
