package com.rafaelsousa.algashop.ordering.core.ports.out.order;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationCommand;

public interface ForPublishingOrderIntegrationCommands {
	void send(IntegrationCommand command);
}
