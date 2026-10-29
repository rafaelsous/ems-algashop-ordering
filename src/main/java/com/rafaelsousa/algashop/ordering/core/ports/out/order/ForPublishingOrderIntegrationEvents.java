package com.rafaelsousa.algashop.ordering.core.ports.out.order;

import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationEvent;

public interface ForPublishingOrderIntegrationEvents {
	void send(OutboundIntegrationEvent event);
}
