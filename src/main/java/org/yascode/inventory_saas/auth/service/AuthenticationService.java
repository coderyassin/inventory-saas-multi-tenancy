package org.yascode.inventory_saas.auth.service;

import org.yascode.inventory_saas.auth.requests.LoginRequest;
import org.yascode.inventory_saas.auth.responses.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(final LoginRequest request);
}
