package com.rafaelsousa.algashop.ordering.core.ports.in.customer;

import com.rafaelsousa.algashop.ordering.core.domain.model.customer.LoyaltyPoints;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Order;

import java.util.UUID;

public interface ForAddingLoyaltyPoints {
    void addLoyaltyPoints(UUID rawCustomerId, String rawOrderId);
    LoyaltyPoints getCalculatedLoyaltyPoints(Order order);
}
