package com.rafaelsousa.algashop.ordering.core.application.order;

import com.rafaelsousa.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.OrderFilter;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderSummaryOutput;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForObtainingOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQueryService implements ForQueryingOrders {
    private final ForObtainingOrders forObtainingOrders;

    @Override
    public OrderDetailOutput findById(String id) {
        return forObtainingOrders.findById(id);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        return forObtainingOrders.filter(filter);
    }
}