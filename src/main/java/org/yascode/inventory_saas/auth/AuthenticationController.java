package org.yascode.inventory_saas.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yascode.inventory_saas.auth.requests.LoginRequest;
import org.yascode.inventory_saas.auth.responses.LoginResponse;
import org.yascode.inventory_saas.auth.service.AuthenticationService;
import org.yascode.inventory_saas.requests.RegisterTenantRequest;
import org.yascode.inventory_saas.services.TenantService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TenantService tenantService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody
            final LoginRequest request
    ) {
        final LoginResponse response = this.authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid
            @RequestBody
            final RegisterTenantRequest request
    ) {
        this.tenantService.registerTenant(request);
        return ResponseEntity.ok().build();
    }
}
