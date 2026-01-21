package com.rafaelsousa.algashop.ordering.core.ports.in.checkout;

import org.springframework.transaction.annotation.Transactional;

public interface ForBuyingWithShoppingCart {
    @Transactional
    String checkout(CheckoutInput checkoutInput);
}