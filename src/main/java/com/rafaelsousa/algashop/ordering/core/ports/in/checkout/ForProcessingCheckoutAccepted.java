package com.rafaelsousa.algashop.ordering.core.ports.in.checkout;

import com.rafaelsousa.algashop.ordering.core.application.order.event.CheckoutAcceptedIntegrationEvent;

public interface ForProcessingCheckoutAccepted {
	void process(CheckoutAcceptedIntegrationEvent event);
}
