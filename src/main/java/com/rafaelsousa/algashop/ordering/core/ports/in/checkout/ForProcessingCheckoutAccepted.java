package com.rafaelsousa.algashop.ordering.core.ports.in.checkout;

import com.rafaelsousa.algashop.ordering.core.application.checkout.command.ProcessAcceptedCheckoutIntegrationCommand;

public interface ForProcessingCheckoutAccepted {
	void process(ProcessAcceptedCheckoutIntegrationCommand command);
}
