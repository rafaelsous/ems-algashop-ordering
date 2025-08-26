package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.DomainService;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import lombok.RequiredArgsConstructor;

import java.time.Year;

@DomainService
@RequiredArgsConstructor
public class BuyNowService {
    private final Orders orders;

    public Order buyNow(Product product, Customer customer, Billing billing, Shipping shipping,
                        Quantity quantity, PaymentMethod paymentMethod) {
        product.checkOutOfStock();

        Order order = Order.draft(customer.id());
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);

        order.addItem(product, quantity);

        if (haveFreeShipping(customer)) {
            Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }

        order.place();

        return order;
    }

    private boolean haveFreeShipping(Customer customer) {
        return customer.loyaltyPoints().compareTo(new LoyaltyPoints(100)) >= 0
                && orders.salesQuantityByCustomerInYear(customer.id(), Year.now()).value() >= 2
                || customer.loyaltyPoints().compareTo(new LoyaltyPoints(2000)) >= 0;
    }
}