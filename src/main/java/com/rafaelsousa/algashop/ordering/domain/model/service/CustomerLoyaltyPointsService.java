package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.exception.CantAddLoyaltyPointsOrderIsNotReady;
import com.rafaelsousa.algashop.ordering.domain.model.exception.OrderNotBelongsToCustomerException;
import com.rafaelsousa.algashop.ordering.domain.model.utils.DomainService;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;

import java.util.Objects;

@DomainService
public class CustomerLoyaltyPointsService {
    private static final LoyaltyPoints basePoints = new LoyaltyPoints(5);
    private static final Money expectedAmountToGivePoints = Money.of("1000.00");

    public void addPoints(Customer customer, Order order) {
        Objects.requireNonNull(customer);
        Objects.requireNonNull(order);

        if (!customer.id().equals(order.customerId())) {
            throw new OrderNotBelongsToCustomerException(customer.id(), order.id());
        }

        if (!order.isReady()) {
            throw new CantAddLoyaltyPointsOrderIsNotReady(order.id());
        }

        customer.addLoyaltyPoints(calculatePOints(order));
    }

    private LoyaltyPoints calculatePOints(Order order) {
        if (shouldGivePointsByAmount(order.totalAmount())) {
            Money result = order.totalAmount().divide(expectedAmountToGivePoints);

            return new LoyaltyPoints(result.value().intValue() * basePoints.value());
        }

        return LoyaltyPoints.ZERO;
    }

    private boolean shouldGivePointsByAmount(Money amount) {
        return amount.compareTo(expectedAmountToGivePoints) >= 0;
    }
}