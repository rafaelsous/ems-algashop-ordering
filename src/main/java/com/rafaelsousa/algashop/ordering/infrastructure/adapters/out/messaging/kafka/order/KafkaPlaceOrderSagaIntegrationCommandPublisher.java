package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.kafka.order;

import com.rafaelsousa.algashop.ordering.core.application.CommandPublishingException;
import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.saga.ForPublishingPlaceOrderSagaIntegrationCommands;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.saga.PlaceOrderSagaCommandTopics;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "false", matchIfMissing = true)
public class KafkaPlaceOrderSagaIntegrationCommandPublisher
        implements ForPublishingPlaceOrderSagaIntegrationCommands {
	private final PlaceOrderSagaCommandTopics commandTopics;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(String correlationId, OutboundIntegrationCommand command) {
		Objects.requireNonNull(correlationId);
	
		SendResult<String, Object> result;
		try {
			ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
				commandTopics.of(command), command.getAggregateId(), command);
			producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, correlationId.getBytes(StandardCharsets.UTF_8));
			producerRecord.headers().add(KafkaHeaders.REPLY_TOPIC, properties.getSagaRepliesTopicName().getBytes(StandardCharsets.UTF_8));

			if (command.getIdempotencyKey() != null) {
				producerRecord.headers().add(KafkaConfig.IDEMPOTENCY_KEY_HEADER,
					command.getIdempotencyKey().toString().getBytes(StandardCharsets.UTF_8));
			}

			result = kafkaTemplate.send(producerRecord).get(40, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new CommandPublishingException("Interrupted while publishing", command, ex);
		} catch (TimeoutException | ExecutionException | KafkaException ex) {
			throw new CommandPublishingException("Failed to publish command", command, ex);
		}
	
		RecordMetadata metadata = result.getRecordMetadata();
	
		log.info(
			"Published {} to {}-{} at offset {}",
			command.getClass().getSimpleName(),
			metadata.topic(),
			metadata.partition(),
			metadata.offset());
	}
}
