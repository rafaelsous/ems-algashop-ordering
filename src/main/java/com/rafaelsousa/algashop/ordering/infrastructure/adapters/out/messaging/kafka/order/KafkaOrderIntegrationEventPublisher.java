package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.kafka.order;

import com.rafaelsousa.algashop.ordering.core.application.EventPublishingException;
import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationEvents;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import com.rafaelsousa.algashop.ordering.infrastructure.config.utility.BeanValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderIntegrationEventPublisher implements ForPublishingOrderIntegrationEvents {
	private final BeanValidationUtil beanValidationUtil;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(IntegrationEvent event) {
		beanValidationUtil.validate(event);

		SendResult<String, Object> result;
		try {
			ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(properties.getOrderEventTopicName(), event.getAggregateId(), event);

			if (event.getIdempotencyKey() != null) {
				producerRecord.headers().add("idempotency-key", event.getIdempotencyKey().toString().getBytes());
			}

			result = kafkaTemplate.send(producerRecord).get(40, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new EventPublishingException("Interrupted while publishing", event, ex);
		} catch (TimeoutException | ExecutionException ex) {
			throw new EventPublishingException("Failed to publish event", event, ex);
		}

		RecordMetadata metadata = result.getRecordMetadata();

		log.info(
			"Published {} to {}-{} at offset {}",
			event.getClass().getSimpleName(),
			metadata.topic(),
			metadata.partition(),
			metadata.offset());
	}
}
