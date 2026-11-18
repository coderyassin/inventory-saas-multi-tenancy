package org.yascode.inventory_saas.event;

import java.time.LocalDateTime;

public record UserCreatedEvent(
        String userId,
        String email,
        LocalDateTime createdAt
) {
}
