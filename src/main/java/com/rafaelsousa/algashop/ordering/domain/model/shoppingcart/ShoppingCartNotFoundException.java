package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;

public class ShoppingCartNotFoundException extends DomainException {

    public ShoppingCartNotFoundException(ShoppingCartId shoppingCartId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND.formatted(shoppingCartId.value()));
    }
}