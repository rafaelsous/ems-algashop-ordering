package com.rafaelsousa.algashop.ordering.core.ports.in.shopping;

import java.util.UUID;

public interface ForQueryingShoppingCarts {
    ShoppingCartOutput findById(UUID shoppingCartId);
    ShoppingCartOutput findByCustomerId(UUID customerId);
}