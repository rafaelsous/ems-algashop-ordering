package com.rafaelsousa.algashop.ordering.infrastructure.listener.order;

import com.rafaelsousa.algashop.ordering.domain.model.order.OrderCanceledEvent;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderPaidEvent;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderPlacedEvent;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    @EventListener
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("OrderPlacedEvent listen");
    }

    @EventListener
    public void listen(OrderPaidEvent orderPaidEvent) {
        log.info("OrderPaidEvent listen");
    }

    @EventListener
    public void listen(OrderReadyEvent orderReadyEvent) {
        log.info("OrderReadyEvent listen");
    }

    @EventListener
    public void listen(OrderCanceledEvent orderCanceledEvent) {
        log.info("OrderCanceledEvent listen");
    }
}