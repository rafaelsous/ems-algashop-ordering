package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;

public class ShoppingCartCantProceedToCheckoutException extends DomainException {

    public ShoppingCartCantProceedToCheckoutException(ShoppingCartId shoppingCartId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_CANT_PROCEED_TO_CHECKOUT.formatted(shoppingCartId));
    }
}