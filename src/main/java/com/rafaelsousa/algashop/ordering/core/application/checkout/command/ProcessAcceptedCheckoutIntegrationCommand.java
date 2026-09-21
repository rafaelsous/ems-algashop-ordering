package com.rafaelsousa.algashop.ordering.core.application.checkout.command;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.domain.model.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessAcceptedCheckoutIntegrationCommand implements IntegrationCommand {
	private UUID idempotencyKey = IdGenerator.generateTimeBasedUUID();

	private OrderSnapshot order;

	public ProcessAcceptedCheckoutIntegrationCommand(OrderSnapshot order) {
		this.order = order;
	}

	@Override
	public String getAggregateId() {
		return order.orderId();
	}

	@Override
	public UUID getIdempotencyKey() {
		return idempotencyKey;
	}
}
