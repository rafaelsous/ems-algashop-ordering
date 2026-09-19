package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.listener.order;

import com.rafaelsousa.algashop.ordering.core.application.order.event.OrderPlacedIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderCanceledEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderPaidEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderPlacedEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationEvents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final Mapper mapper;
    private final ForPublishingOrderIntegrationEvents forPublishingOrderIntegrationEvents;

    @EventListener
    public void listen(OrderPlacedEvent event) {
        OrderPlacedIntegrationEvent integrationEvent = mapper.convert(event, OrderPlacedIntegrationEvent.class);
        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void listen(OrderPaidEvent event) {
        log.info("OrderPaidEvent listen. Event={}", event);
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        log.info("OrderReadyEvent listen. Event={}", event);
    }

    @EventListener
    public void listen(OrderCanceledEvent event) {
        log.info("OrderCanceledEvent listen. Event={}", event);
    }
}