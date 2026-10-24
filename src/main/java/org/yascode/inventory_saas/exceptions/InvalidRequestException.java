package org.yascode.inventory_saas.exceptions;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException(final String message) {
        super(message);
    }
}
