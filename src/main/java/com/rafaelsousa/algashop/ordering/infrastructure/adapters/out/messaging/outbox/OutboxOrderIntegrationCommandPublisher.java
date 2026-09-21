package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationCommands;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
public class OutboxOrderIntegrationCommandPublisher implements ForPublishingOrderIntegrationCommands {
	private final OutboxRecorder recorder;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(IntegrationCommand command) {
		recorder.record(properties.getOrderCommandTopicName(), command.getAggregateId(), command);
	}
}
