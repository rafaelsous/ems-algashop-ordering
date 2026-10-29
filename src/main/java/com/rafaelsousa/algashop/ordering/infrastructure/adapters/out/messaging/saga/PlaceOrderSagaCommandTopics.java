package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.saga;

import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.application.invoice.command.IssueInvoiceIntegrationCommand;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceOrderSagaCommandTopics {
	private final AlgaShopMessagingKafkaProperties properties;

	public String of(OutboundIntegrationCommand command) {
		return switch (command) {
			case IssueInvoiceIntegrationCommand ignored -> properties.getInvoiceCommandsTopicName();
			default -> throw new IllegalArgumentException("Unsupported saga command " + command.getClass().getSimpleName());
		};
	}
}
