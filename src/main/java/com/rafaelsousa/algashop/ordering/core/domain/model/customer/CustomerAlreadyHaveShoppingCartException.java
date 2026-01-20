package com.rafaelsousa.algashop.ordering.core.domain.model.customer;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;

public class CustomerAlreadyHaveShoppingCartException extends DomainException {

    public CustomerAlreadyHaveShoppingCartException(CustomerId customerId, ShoppingCartId shoppingCartId) {
        super(ErrorMessages.ERROR_CUSTOMER_ALREADY_HAVE_SHOPPING_CART.formatted(customerId, shoppingCartId));
    }
}