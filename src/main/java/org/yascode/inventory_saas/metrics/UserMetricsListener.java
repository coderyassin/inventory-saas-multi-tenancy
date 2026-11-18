package org.yascode.inventory_saas.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.yascode.inventory_saas.event.UserCreatedEvent;

@Component
@RequiredArgsConstructor
public class UserMetricsListener {

    private final BusinessMetricsService metricsService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onUserCreated(UserCreatedEvent event) {

        metricsService.incrementUsersCreated();
    }
}
