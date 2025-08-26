package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.Specification;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.LoyaltyPoints;

public class CustomerHaveFreeShippingSpecification implements Specification<Customer> {
    private final CustomerHasEnoughLoyaltyPointsSpecification hasEnoughBasicLoyaltyPointsSpecification;
    private final CustomerHasEnoughLoyaltyPointsSpecification hasEnoughPremiumLoyaltyPointsSpecification;
    private final CustomerHasOrderedEnoughAtYearSpecification hasOrderedEnoughAtYearSpecification;

    public CustomerHaveFreeShippingSpecification(Orders orders,
                                                 LoyaltyPoints basicLoyaltyPoints,
                                                 Quantity salesQuantityForFreeShipping,
                                                 LoyaltyPoints premiumLoyaltyPoints) {
        this.hasOrderedEnoughAtYearSpecification = new CustomerHasOrderedEnoughAtYearSpecification(orders, salesQuantityForFreeShipping);
        this.hasEnoughBasicLoyaltyPointsSpecification = new CustomerHasEnoughLoyaltyPointsSpecification(basicLoyaltyPoints);
        this.hasEnoughPremiumLoyaltyPointsSpecification = new CustomerHasEnoughLoyaltyPointsSpecification(premiumLoyaltyPoints);
    }

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return hasEnoughBasicLoyaltyPointsSpecification
                .and(hasOrderedEnoughAtYearSpecification)
                .or(hasEnoughPremiumLoyaltyPointsSpecification)
                .isSatisfiedBy(customer);
    }
}