package com.rafaelsousa.algashop.ordering.core.application.order;

import com.rafaelsousa.algashop.ordering.core.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Orders;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.ForManagingOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderManagementApplicationService implements ForManagingOrders {
    private final Orders orders;

    @Override
    @Transactional
    public void cancel(Long rawOrderId) {
        Objects.requireNonNull(rawOrderId);

        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException(new OrderId(rawOrderId)));

        order.cancel();

        orders.add(order);
    }

    @Override
    @Transactional
    public void markAsPaid(Long rawOrderId) {
        Objects.requireNonNull(rawOrderId);

        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException(new OrderId(rawOrderId)));

        order.markAsPaid();

        orders.add(order);
    }

    @Override
    @Transactional
    public void markAsReady(Long rawOrderId) {
        Objects.requireNonNull(rawOrderId);

        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException(new OrderId(rawOrderId)));

        order.markAsReady();

        orders.add(order);
    }
}