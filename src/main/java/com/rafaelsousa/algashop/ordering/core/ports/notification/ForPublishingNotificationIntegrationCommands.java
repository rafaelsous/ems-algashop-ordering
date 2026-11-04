package com.rafaelsousa.algashop.ordering.core.ports.notification;

import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationCommand;

public interface ForPublishingNotificationIntegrationCommands {
	void send(OutboundIntegrationCommand command);
}
