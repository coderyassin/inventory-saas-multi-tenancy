package org.yascode.inventory_saas.analytics.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yascode.inventory_saas.analytics.application.OutboxBatchProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxBatchProcessor processor;

    @Scheduled(fixedDelayString =
            "${app.analytics.outbox.fixed-delay:2000}")
    public void processOutbox() {

        try {
            int processed = processor.processBatch();

            if (processed > 0) {
                log.info(
                        "Business outbox: {} events processed",
                        processed
                );
            }

        } catch (Exception exception) {
            log.error(
                    "Business outbox processing failed",
                    exception
            );
        }
    }
}
