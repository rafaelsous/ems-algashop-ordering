package com.rafaelsousa.algashop.ordering.core.application.checkout.command;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessAcceptedCheckoutIntegrationCommand implements IntegrationCommand {

	private OrderSnapshot order;

	@Override
	public String getAggregateId() {
		return order.orderId();
	}
}
