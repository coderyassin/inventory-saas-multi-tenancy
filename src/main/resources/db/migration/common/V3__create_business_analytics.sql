
-- 1. Durable business events
CREATE TABLE public.business_outbox_events (
                                               id UUID PRIMARY KEY,
                                               event_type VARCHAR(100) NOT NULL,
                                               aggregate_id VARCHAR(100) NOT NULL,
                                               occurred_at TIMESTAMPTZ NOT NULL,
                                               status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                               processed_at TIMESTAMPTZ,

                                               CONSTRAINT chk_outbox_status
                                                   CHECK (status IN ('PENDING', 'PROCESSED'))
);

CREATE INDEX idx_business_outbox_pending
    ON public.business_outbox_events (occurred_at, id)
    WHERE status = 'PENDING';


-- 2. Daily business statistics
CREATE TABLE public.business_daily_stats (
                                             stat_date DATE NOT NULL,
                                             metric_name VARCHAR(100) NOT NULL,
                                             metric_value BIGINT NOT NULL DEFAULT 0,

                                             CONSTRAINT pk_business_daily_stats
                                                 PRIMARY KEY (stat_date, metric_name),

                                             CONSTRAINT chk_metric_value
                                                 CHECK (metric_value >= 0)
);
