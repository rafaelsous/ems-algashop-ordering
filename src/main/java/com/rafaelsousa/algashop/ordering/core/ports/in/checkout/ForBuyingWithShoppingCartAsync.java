package com.rafaelsousa.algashop.ordering.core.ports.in.checkout;

public interface ForBuyingWithShoppingCartAsync {
    String checkout(CheckoutInput checkoutInput);
}
