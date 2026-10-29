ALTER TABLE public.outbox ADD COLUMN correlation_id VARCHAR(255);
ALTER TABLE public.outbox ADD COLUMN reply_topic VARCHAR(255);