package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.ports.notification.ForPublishingNotificationIntegrationCommands;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
public class OutboxNotificationIntegrationCommandPublisher implements ForPublishingNotificationIntegrationCommands {
	private final OutboxRecorder recorder;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(OutboundIntegrationCommand command) {
		recorder.record(OutboxDraft.builder()
			.channelName(properties.getNotificationsEmailCommandsTopicName())
			.aggregateId(command.getAggregateId())
			.payload(command)
			.build());
	}
}
