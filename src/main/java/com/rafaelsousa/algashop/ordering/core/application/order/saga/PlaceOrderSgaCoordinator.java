package com.rafaelsousa.algashop.ordering.core.application.order.saga;

import com.rafaelsousa.algashop.ordering.core.application.invoice.command.IssueInvoiceIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.application.order.snapshot.OrderSnapshot;
import com.rafaelsousa.algashop.ordering.core.application.order.snapshot.OrderSnapshotAssembler;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Orders;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.saga.ForCoordinatingPlaceOrderSaga;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.saga.ForPublishingPlaceOrderSagaIntegrationCommands;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.saga.ForStoringPlaceOrderSagas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceOrderSgaCoordinator implements ForCoordinatingPlaceOrderSaga {
    private final Orders orders;
    private final ForStoringPlaceOrderSagas sagas;
    private final OrderSnapshotAssembler orderSnapshotAssembler;
    private final ForPublishingPlaceOrderSagaIntegrationCommands commands;

    @Override
    @Transactional
    public void start(String rawOrderId) {
        OrderId orderId = new OrderId(rawOrderId);
        Order order = findOrder(orderId);

	    OrderSnapshot snapshot = orderSnapshotAssembler.toSnapshot(order);
	    IssueInvoiceIntegrationCommand command = IssueInvoiceIntegrationCommand.from(snapshot);

	    PlaceOrderSaga saga = PlaceOrderSaga.start(orderId);
		saga.moveToInvoicing();
	    sagas.add(saga);

	    commands.send(saga.sagaId().toString(), command);

        log.info("Place order saga started: saga={} order={}", saga.sagaId(), saga.orderId());
    }

    @Override
    @Transactional
    public void onInvoicePaid(UUID sagaId) {
        PlaceOrderSaga saga = findSaga(sagaId);

	    Order order = findOrder(saga.orderId());
	    order.markAsPaid();
		orders.add(order);

		// TODO: Add more logic here
    }

    private Order findOrder(OrderId orderId) {
        return orders.ofId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

	private PlaceOrderSaga findSaga(UUID sagaId) {
		return sagas.ofId(sagaId)
				.orElseThrow(() -> new PlaceOrderSagaNotFoundException(sagaId));
	}
}
