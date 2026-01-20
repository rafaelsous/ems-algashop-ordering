package com.rafaelsousa.algashop.ordering.core.domain.model.order;

import com.rafaelsousa.algashop.ordering.core.domain.model.Specification;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.Customer;
import lombok.RequiredArgsConstructor;

import java.time.Year;

@RequiredArgsConstructor
public class CustomerHasOrderedEnoughAtYearSpecification implements Specification<Customer> {
    private final Orders orders;
    private final Quantity expectedOrderCount;

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return orders.salesQuantityByCustomerInYear(customer.id(), Year.now()).compareTo(expectedOrderCount) >= 0;
    }
}