package com.rafaelsousa.algashop.ordering.core.ports.out.order;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;

public interface ForPublishingOrderIntegrationEvents {
	void send(IntegrationEvent event);
}
