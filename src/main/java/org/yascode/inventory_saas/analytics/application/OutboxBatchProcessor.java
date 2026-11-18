package org.yascode.inventory_saas.analytics.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxBatchProcessor {

    private final JdbcTemplate jdbcTemplate;

    private static final int BATCH_SIZE = 100;

    private record OutboxEvent(
            UUID id,
            String eventType,
            OffsetDateTime occurredAt
    ) {}

    @Transactional
    public int processBatch() {
        log.info("Starting outbox batch processing with batch size: {}", BATCH_SIZE);

        String selectSql = """
            SELECT id, event_type, occurred_at
            FROM public.business_outbox_events
            WHERE status = 'PENDING'
            ORDER BY occurred_at, id
            LIMIT ?
            FOR UPDATE SKIP LOCKED
            """;

        List<OutboxEvent> events = jdbcTemplate.query(
                selectSql,
                (rs, rowNum) -> new OutboxEvent(
                        rs.getObject("id", UUID.class),
                        rs.getString("event_type"),
                        rs.getObject("occurred_at", OffsetDateTime.class)
                ),
                BATCH_SIZE
        );

        log.info("Fetched {} pending outbox event(s)", events.size());

        for (OutboxEvent event : events) {
            log.debug("Processing outbox event with id: {}, type: {}, occurredAt: {}",
                    event.id(), event.eventType(), event.occurredAt());

            switch (event.eventType()) {

                case "USER_CREATED" ->
                        incrementDailyUserCreation(event);

                default -> {
                    log.error("Unsupported outbox event type: {} for event id: {}", event.eventType(), event.id());
                    throw new IllegalArgumentException(
                            "Unsupported event type: " + event.eventType()
                    );
                }
            }

            markAsProcessed(event.id());
            log.debug("Outbox event processed successfully with id: {}", event.id());
        }

        log.info("Completed outbox batch processing. Processed {} event(s)", events.size());
        return events.size();
    }

    private void incrementDailyUserCreation(OutboxEvent event) {
        log.debug("Incrementing daily user creation metric for event id: {}", event.id());

        String sql = """
            INSERT INTO public.business_daily_stats (
                stat_date,
                metric_name,
                metric_value
            )
            VALUES (
                (?::timestamptz AT TIME ZONE 'UTC')::date,
                'users_created',
                1
            )
            ON CONFLICT (stat_date, metric_name)
            DO UPDATE SET
                metric_value =
                    public.business_daily_stats.metric_value + 1
            """;

        jdbcTemplate.update(sql, event.occurredAt());

        log.debug("Daily user creation metric incremented for event id: {}", event.id());
    }

    private void markAsProcessed(UUID eventId) {
        log.debug("Marking outbox event as processed with id: {}", eventId);

        String sql = """
            UPDATE public.business_outbox_events
            SET status = 'PROCESSED',
                processed_at = CURRENT_TIMESTAMP
            WHERE id = ?
              AND status = 'PENDING'
            """;

        jdbcTemplate.update(sql, eventId);

        log.debug("Outbox event marked as processed with id: {}", eventId);
    }
}