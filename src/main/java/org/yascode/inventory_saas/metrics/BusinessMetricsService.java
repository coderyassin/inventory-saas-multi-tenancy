package org.yascode.inventory_saas.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetricsService {

    private final Counter usersCreatedCounter;

    public BusinessMetricsService(MeterRegistry meterRegistry) {

        this.usersCreatedCounter = Counter.builder(
                        "inventory.users.created"
                )
                .description("Total number of successfully created users")
                .register(meterRegistry);
    }

    public void incrementUsersCreated() {
        usersCreatedCounter.increment();
    }
}
