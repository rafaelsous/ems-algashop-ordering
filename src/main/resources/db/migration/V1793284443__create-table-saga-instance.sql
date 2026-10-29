CREATE TABLE IF NOT EXISTS public.saga_instance (
    saga_id      uuid                     NOT NULL,
    saga_type    VARCHAR(50)              NOT NULL,
    aggregate_id VARCHAR(100)             NOT NULL,
    status       VARCHAR(30)              NOT NULL,
    step         VARCHAR(50)              NOT NULL,
    failure      VARCHAR(50),
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    version      BIGINT                   NOT NULL,
    PRIMARY KEY (saga_id),
    CONSTRAINT uk_saga_instance_type_aggregate UNIQUE (saga_type, aggregate_id)
);

CREATE INDEX idx_saga_instance_active
    ON public.saga_instance (saga_type, status)
    WHERE status in ('RUNNING', 'COMPENSATING');