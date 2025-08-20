package com.rafaelsousa.algashop.ordering.domain.model.exception;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

public class CustomerAlreadyHaveShoppingCartException extends DomainException {

    public CustomerAlreadyHaveShoppingCartException(CustomerId customerId, ShoppingCartId shoppingCartId) {
        super(ErrorMessages.ERROR_CUSTOMER_ALREADY_HAVE_SHOPPING_CART.formatted(customerId, shoppingCartId));
    }
}