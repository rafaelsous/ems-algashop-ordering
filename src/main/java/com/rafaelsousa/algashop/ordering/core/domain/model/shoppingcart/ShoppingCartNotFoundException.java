package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;

public class ShoppingCartNotFoundException extends DomainEntityNotFoundException {

    public ShoppingCartNotFoundException(ShoppingCartId shoppingCartId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND.formatted(shoppingCartId.value()));
    }

    public ShoppingCartNotFoundException(CustomerId customerId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND_FOR_CUSTOMER.formatted(customerId.value()));
    }
}