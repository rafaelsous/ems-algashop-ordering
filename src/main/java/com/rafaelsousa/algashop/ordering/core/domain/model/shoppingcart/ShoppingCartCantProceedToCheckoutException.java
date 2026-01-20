package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;

public class ShoppingCartCantProceedToCheckoutException extends DomainException {

    public ShoppingCartCantProceedToCheckoutException(ShoppingCartId shoppingCartId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_CANT_PROCEED_TO_CHECKOUT.formatted(shoppingCartId));
    }
}