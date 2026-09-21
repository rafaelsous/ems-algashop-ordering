package com.rafaelsousa.algashop.ordering.core.application.order.event;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.IdGenerator;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedIntegrationEvent implements IntegrationEvent {
    private String orderId;
    private UUID customerId;
    private OffsetDateTime placedAt;
	private UUID idempotencyKey = IdGenerator.generateTimeBasedUUID();

	public OrderPlacedIntegrationEvent(String orderId, UUID customerId, OffsetDateTime placedAt) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.placedAt = placedAt;
	}

	@Override
	public String getAggregateId() {
		return orderId;
	}
}
