package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.messaging.kafka.order;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.application.checkout.command.ProcessAcceptedCheckoutIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ForProcessingCheckoutAccepted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
	id = "ordering.order-commands",
	topics = "#{algaShopMessagingKafkaProperties.orderCommandTopicName}"
)
@RequiredArgsConstructor
public class KafkaOrderIntegrationCommandListener {
	private final ForProcessingCheckoutAccepted forProcessingCheckoutAccepted;

	@KafkaHandler(isDefault = true)
	public void handle(Object integrationCommand) {
		log.info("Order integration command ignored: {}", integrationCommand.getClass().getSimpleName());
	}

	@KafkaHandler
	public void handle(
		@Payload ProcessAcceptedCheckoutIntegrationCommand integrationCommand,
		@Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
		@Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
		@Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
		log(integrationCommand, messageKey, partition, offset);

		forProcessingCheckoutAccepted.process(integrationCommand);
	}

	private static void log(IntegrationCommand command, String messageKey, Integer partition, Integer offset) {
		log.info("Received command: {} with key: {}, partition: {}, offset: {}, and thread: {}",
			command.getClass().getSimpleName(), messageKey, partition, offset, Thread.currentThread().getName());
	}
}
