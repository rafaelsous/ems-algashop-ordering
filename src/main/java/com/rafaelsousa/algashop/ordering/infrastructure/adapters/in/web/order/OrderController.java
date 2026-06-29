package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order;

import com.rafaelsousa.algashop.ordering.core.ports.in.order.*;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderSummaryOutput;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.PageModel;
import com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.CanReadOrders;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final ForQueryingOrders forQueryingOrders;

    @GetMapping("/{orderId}")
    @CanReadOrders
    public OrderDetailOutput findById(@PathVariable String orderId) {
        return forQueryingOrders.findById(orderId);
    }

    @GetMapping
    @CanReadOrders
    public PageModel<OrderSummaryOutput> filter(OrderFilter filter) {
        return PageModel.of(forQueryingOrders.filter(filter));
    }
}
