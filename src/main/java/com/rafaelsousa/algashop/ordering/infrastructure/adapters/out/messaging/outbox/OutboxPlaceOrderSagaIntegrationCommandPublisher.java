package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.saga.ForPublishingPlaceOrderSagaIntegrationCommands;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.saga.PlaceOrderSagaCommandTopics;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
public class OutboxPlaceOrderSagaIntegrationCommandPublisher
        implements ForPublishingPlaceOrderSagaIntegrationCommands {
    private final OutboxRecorder recorder;
	private final PlaceOrderSagaCommandTopics commandTopics;
	private final AlgaShopMessagingKafkaProperties properties;

    @Override
    public void send(String correlationId, OutboundIntegrationCommand command) {
        recorder.record(
                OutboxDraft.builder()
                        .aggregateId(command.getAggregateId())
                        .correlationId(correlationId)
                        .payload(command)
                        .channelName(commandTopics.of(command))
                        .replyTopic(properties.getSagaRepliesTopicName())
                        .build());
    }
}
