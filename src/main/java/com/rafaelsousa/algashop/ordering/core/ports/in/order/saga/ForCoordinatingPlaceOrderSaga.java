package com.rafaelsousa.algashop.ordering.core.ports.in.order.saga;

public interface ForCoordinatingPlaceOrderSaga {
	void start(String rawOrderId);
}
