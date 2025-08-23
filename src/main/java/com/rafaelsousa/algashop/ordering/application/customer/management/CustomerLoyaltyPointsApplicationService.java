package com.rafaelsousa.algashop.ordering.application.customer.management;

import com.rafaelsousa.algashop.ordering.domain.model.customer.*;
import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyPointsApplicationService {
    private final Orders orders;
    private final Customers customers;
    private final CustomerLoyaltyPointsService customerLoyaltyPointsService;

    @Transactional
    public void addLoyaltyPoints(UUID rawCustomerId, String rawOrderId) {
        Objects.requireNonNull(rawCustomerId);
        Objects.requireNonNull(rawOrderId);

        CustomerId customerId = new CustomerId(rawCustomerId);
        Customer customer = customers.ofId(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        OrderId orderId = new OrderId(rawOrderId);
        Order order = orders.ofId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        customerLoyaltyPointsService.addPoints(customer, order);

        customers.add(customer);
    }

    protected LoyaltyPoints getCalculatedLoyaltyPoints(Order order) {
        Objects.requireNonNull(order);

        return customerLoyaltyPointsService.getCalculatedPoints(order);
    }
}