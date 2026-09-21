package com.rafaelsousa.algashop.ordering.core.application.checkout;

import com.rafaelsousa.algashop.ordering.core.application.checkout.command.ProcessAcceptedCheckoutIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.application.order.event.OrderPlacedIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.checkout.command.OrderSnapshotAssembler;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Orders;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ForProcessingCheckoutAccepted;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationEvents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessCheckoutAcceptedApplicationService implements ForProcessingCheckoutAccepted {
	private final Orders orders;
	private final ShoppingCarts shoppingCarts;
	private final OrderSnapshotAssembler orderSnapshotAssembler;
	private final ForPublishingOrderIntegrationEvents forPublishingOrderIntegrationEvents;

	@Override
	@Transactional
	public void process(ProcessAcceptedCheckoutIntegrationCommand command) {
		OrderId orderId = new OrderId(command.getAggregateId());

		if (orders.exists(orderId)) {
			log.info("Order with ID {} already exists. Skipping processing.", orderId);
			return;
		}

		Order order = orderSnapshotAssembler.toDomain(command.getOrder());
		orders.add(order);

		UUID rawShoppingCartId = command.getOrder().shoppingCartId();
		if (rawShoppingCartId != null) {
			shoppingCarts.ofId(new ShoppingCartId(rawShoppingCartId)).ifPresent(shoppingCart -> {
				shoppingCart.empty();
				shoppingCarts.add(shoppingCart);
			});
		}

        forPublishingOrderIntegrationEvents.send(
			new OrderPlacedIntegrationEvent(
				orderId.toString(),
				order.customerId().value(),
				order.placedAt())
			);
	}
}
