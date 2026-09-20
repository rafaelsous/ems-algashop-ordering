package com.rafaelsousa.algashop.ordering.core.application.order.event;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutAcceptedIntegrationEvent implements IntegrationEvent {

	private OrderSnapshot order;

	@Override
	public String getAggregateId() {
		return order.orderId();
	}
}
