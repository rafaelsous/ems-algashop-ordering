package com.rafaelsousa.algashop.ordering.domain.factory;

import com.rafaelsousa.algashop.ordering.domain.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.entity.PaymentMethod;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Billing;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Shipping;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.CustomerId;

import java.util.Objects;

public class OrderFactory {

    private OrderFactory() {
    }

    public static Order filled(
            CustomerId customerId,
            Shipping shipping,
            Billing billing,
            PaymentMethod paymentMethod,
            Product product,
            Quantity productQuantity
    ) {
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(shipping);
        Objects.requireNonNull(billing);
        Objects.requireNonNull(paymentMethod);
        Objects.requireNonNull(product);
        Objects.requireNonNull(productQuantity);

        Order order = Order.draft(customerId);

        order.changeShipping(shipping);
        order.changeBilling(billing);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, productQuantity);

        return order;
    }
}