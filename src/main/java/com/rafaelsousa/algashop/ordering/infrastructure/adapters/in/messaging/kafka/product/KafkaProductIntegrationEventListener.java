package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.messaging.kafka.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = "product-catalog.product.events")
public class KafkaProductIntegrationEventListener {

	@KafkaHandler
	public void handle(ProductListedIntegrationEvent event, @Header(value = KafkaHeaders.RECEIVED_KEY) String messageKey) {
		log.info("Received {} with key: {} and payload: {}", event.getClass(), messageKey, event);
		// Handle the event (e.g., update the ordering service's product catalog)
	}

	@KafkaHandler
	public void handle(ProductDelistedIntegrationEvent event, @Header(value = KafkaHeaders.RECEIVED_KEY) String messageKey) {
		log.info("Received {} with key: {} and payload: {}", event.getClass(), messageKey, event);
		// Handle the event (e.g., update the ordering service's product catalog)
	}
}
