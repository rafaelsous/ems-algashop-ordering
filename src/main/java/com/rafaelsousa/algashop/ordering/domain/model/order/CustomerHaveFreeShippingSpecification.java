package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.Specification;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.LoyaltyPoints;
import lombok.RequiredArgsConstructor;

import java.time.Year;

@RequiredArgsConstructor
public class CustomerHaveFreeShippingSpecification implements Specification<Customer> {
    private final Orders orders;

    private final int minPointsForFreeShippingRule1;
    private final long minSalesQuantityForFreeShippingRule1;

    private final int minPointsForFreeShippingRule2;

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return customer.loyaltyPoints().compareTo(new LoyaltyPoints(minPointsForFreeShippingRule1)) >= 0
                && orders.salesQuantityByCustomerInYear(customer.id(), Year.now()).value() >= minSalesQuantityForFreeShippingRule1
                || customer.loyaltyPoints().compareTo(new LoyaltyPoints(minPointsForFreeShippingRule2)) >= 0;
    }
}