package org.yascode.inventory_saas.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.yascode.inventory_saas.auth.requests.LoginRequest;
import org.yascode.inventory_saas.auth.responses.LoginResponse;
import org.yascode.inventory_saas.entities.User;
import org.yascode.inventory_saas.security.JwtTokenService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginResponse login(final LoginRequest request) {
        final Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        final User user = (User) authentication.getPrincipal();

        final String token = this.jwtTokenService.generateAccessToken(user.getTenantId(),
                                                                      user.getId(),
                                                                      user.getRole()
                                                                          .name());
        final String tokenType = "Bearer";

        return LoginResponse.builder()
                            .accessToken(token)
                            .tokenType(tokenType)
                            .build();
    }
}
