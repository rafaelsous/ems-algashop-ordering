package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.rafaelsousa.algashop.ordering.domain.model.DomainService;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;

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

        customer.addLoyaltyPoints(calculatePoints(order));
    }

    public LoyaltyPoints getCalculatedPoints(Order order) {
        return calculatePoints(order);
    }

    private LoyaltyPoints calculatePoints(Order order) {
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