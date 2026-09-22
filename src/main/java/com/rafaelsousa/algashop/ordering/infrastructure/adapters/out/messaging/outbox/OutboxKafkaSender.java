package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@ConditionalOnProperty(name = "algashop.messaging.outbox.dispatcher.enabled", havingValue = "true")
public class OutboxKafkaSender {
    private final OutboxProperties outboxProperties;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public OutboxKafkaSender(
            OutboxProperties outboxProperties,
            @Qualifier("outboxKafkaTemplate") KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.outboxProperties = outboxProperties;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(OutboxMessage message) {
	    try {
		    Thread.sleep(Duration.ofSeconds(40));
	    } catch (InterruptedException _) {
		    Thread.currentThread().interrupt();
	    }

	    ProducerRecord<String, byte[]> record =
                new ProducerRecord<>(
                        message.getChannelName(),
                        message.getAggregateId(),
                        message.getPayload().getBytes(StandardCharsets.UTF_8));

        record.headers()
                .add(
                        KafkaConfig.TYPE_ID_HEADER,
                        message.getEventType().getBytes(StandardCharsets.UTF_8));
        record.headers()
                .add(
                        KafkaConfig.IDEMPOTENCY_KEY_HEADER,
                        String.valueOf(message.getId()).getBytes(StandardCharsets.UTF_8));

        SendResult<String, byte[]> result = doSend(record);

        RecordMetadata metadata = result.getRecordMetadata();
        log.info(
                "Published {} from outbox to {}-{} at offset {} | messageId={} | aggregateId={}",
                message.getEventType(),
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                message.getId(),
                message.getAggregateId());
    }

    private SendResult<String, byte[]> doSend(ProducerRecord<String, byte[]> record) {
	    try {
		    return kafkaTemplate.send(record).get(outboxProperties.getSendTimeout().toMillis(), TimeUnit.MILLISECONDS);
	    } catch (InterruptedException ex) {
		    Thread.currentThread().interrupt();
		    throw new OutboxSendException("Interrupted while publishing", ex);
	    } catch (ExecutionException | TimeoutException | KafkaException ex) {
		    throw new OutboxSendException("Failed to publish", ex);
	    }
    }
}
