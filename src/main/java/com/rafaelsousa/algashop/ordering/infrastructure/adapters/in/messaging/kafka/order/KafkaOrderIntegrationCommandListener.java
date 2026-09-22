package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.messaging.kafka.order;

import com.rafaelsousa.algashop.ordering.core.application.checkout.command.ProcessAcceptedCheckoutIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ForProcessingCheckoutAccepted;

import com.rafaelsousa.algashop.ordering.core.ports.out.idempotency.ForGuardingIdempotency;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@KafkaListener(
	id = "ordering.order-commands",
	topics = "#{algaShopMessagingKafkaProperties.orderCommandTopicName}"
)
@RequiredArgsConstructor
public class KafkaOrderIntegrationCommandListener {
	private final ForProcessingCheckoutAccepted forProcessingCheckoutAccepted;
	private final ForGuardingIdempotency forGuardingIdempotency;

	@KafkaHandler(isDefault = true)
	public void handle(
        @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
		@Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
		@Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
		log.warn("OrderIntegrationCommand ignored with key: {}, partition: {}, offset: {}, and thread: {}",
			messageKey, partition, offset, Thread.currentThread().getName());

		throw new IllegalArgumentException("Unsupported order command");
	}

	@KafkaHandler
	public void handle(
		@Payload ProcessAcceptedCheckoutIntegrationCommand integrationCommand,
		@Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
		@Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
		@Header(value = KafkaHeaders.OFFSET, required = false) Integer offset,
		@Header(value = KafkaConfig.IDEMPOTENCY_KEY_HEADER) byte[] rawIdempotencyKey) {
		log(integrationCommand, messageKey, partition, offset);

		UUID idempotencyKey = UUID.fromString(new String(rawIdempotencyKey));
		forGuardingIdempotency.runOnce(idempotencyKey, () -> forProcessingCheckoutAccepted.process(integrationCommand));
	}

	private static void log(Object object, String messageKey, Integer partition, Integer offset) {
		log.info("Received object: {} with key: {}, partition: {}, offset: {}, and thread: {}",
			object.getClass().getSimpleName(), messageKey, partition, offset, Thread.currentThread().getName());
	}
}
