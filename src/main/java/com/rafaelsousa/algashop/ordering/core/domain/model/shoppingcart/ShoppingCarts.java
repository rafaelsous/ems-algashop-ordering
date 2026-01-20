package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.RemoveCapableRepository;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;

import java.util.Optional;

public interface ShoppingCarts extends RemoveCapableRepository<ShoppingCart, ShoppingCartId> {
    Optional<ShoppingCart> ofCustomer(CustomerId customerId);
}