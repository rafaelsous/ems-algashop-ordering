package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;

public class CustomerAlreadyHaveShoppingCartException extends DomainException {

    public CustomerAlreadyHaveShoppingCartException(CustomerId customerId, ShoppingCartId shoppingCartId) {
        super(ErrorMessages.ERROR_CUSTOMER_ALREADY_HAVE_SHOPPING_CART.formatted(customerId, shoppingCartId));
    }
}