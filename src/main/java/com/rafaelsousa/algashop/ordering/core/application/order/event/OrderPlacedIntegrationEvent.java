package com.rafaelsousa.algashop.ordering.core.application.order.event;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;
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

	@Override
	public String getAggregateId() {
		return orderId;
	}
}
