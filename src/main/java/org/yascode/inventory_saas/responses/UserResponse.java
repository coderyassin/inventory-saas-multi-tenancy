package org.yascode.inventory_saas.responses;

import lombok.*;
import org.yascode.inventory_saas.entities.UserRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole role;
}
