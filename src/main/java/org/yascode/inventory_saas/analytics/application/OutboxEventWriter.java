package org.yascode.inventory_saas.analytics.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventWriter {

    private final JdbcTemplate jdbcTemplate;

    public void publishUserCreated(String userId) {
        log.info("Publishing USER_CREATED outbox event for user id: {}", userId);

        UUID eventId = UUID.randomUUID();
        OffsetDateTime occurredAt = OffsetDateTime.now(ZoneOffset.UTC);

        String sql = """
            INSERT INTO public.business_outbox_events (
                id,
                event_type,
                aggregate_id,
                occurred_at,
                status
            )
            VALUES (?, ?, ?, ?, 'PENDING')
            """;

        jdbcTemplate.update(
                sql,
                eventId,
                "USER_CREATED",
                userId,
                occurredAt
        );

        log.info("USER_CREATED outbox event published successfully with event id: {}", eventId);
    }
}