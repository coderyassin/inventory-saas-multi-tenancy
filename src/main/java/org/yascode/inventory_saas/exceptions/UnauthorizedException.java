package org.yascode.inventory_saas.exceptions;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(final String message) {
        super(message);
    }
}
