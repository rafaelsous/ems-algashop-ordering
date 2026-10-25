package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.listener.order;

import com.rafaelsousa.algashop.ordering.core.application.order.event.*;
import com.rafaelsousa.algashop.ordering.core.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.*;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationEvents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final Orders orders;
    private final Mapper mapper;
    private final ForPublishingOrderIntegrationEvents forPublishingOrderIntegrationEvents;
    private final OrderPlacedIntegrationEventAssembler orderPlacedIntegrationEventAssembler;
    private final OrderPaidIntegrationEventAssembler orderPaidIntegrationEventAssembler;

    @EventListener
    public void listen(OrderPlacedEvent event) {
        Order order = orders.ofId(event.orderId()).orElseThrow(OrderNotFoundException::new);
        OrderPlacedIntegrationEvent integrationEvent = orderPlacedIntegrationEventAssembler.toIntegrationEvent(order);

        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void listen(OrderPaidEvent event) {
        Order order = orders.ofId(event.orderId()).orElseThrow(OrderNotFoundException::new);
        OrderPaidIntegrationEvent integrationEvent = orderPaidIntegrationEventAssembler.toIntegrationEvent(order);

        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        log.info("OrderReadyEvent listen. Event={}", event);
    }

    @EventListener
    public void listen(OrderCanceledEvent event) {
        OrderCanceledIntegrationEvent integrationEvent = mapper.convert(event, OrderCanceledIntegrationEvent.class);
        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }
}