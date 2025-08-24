package com.rafaelsousa.algashop.ordering.application.order.management;

import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderManagementApplicationService {
    private final Orders orders;

    @Transactional
    public void cancel(Long rawOrderId) {
        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException(new OrderId(rawOrderId)));

        order.cancel();

        orders.add(order);
    }

    @Transactional
    public void markAsPaid(Long rawOrderId) {
        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException(new OrderId(rawOrderId)));

        order.markAsPaid();

        orders.add(order);
    }

    @Transactional
    public void markAsReady(Long rawOrderId) {
        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException(new OrderId(rawOrderId)));

        order.markAsReady();

        orders.add(order);
    }
}