package com.rafaelsousa.algashop.ordering.core.ports.out.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartOutput;

import java.util.UUID;

public interface ForObtainingShoppingCarts {
    ShoppingCartOutput findById(UUID shoppingCartId);
    ShoppingCartOutput findByCustomerId(UUID customerId);
}