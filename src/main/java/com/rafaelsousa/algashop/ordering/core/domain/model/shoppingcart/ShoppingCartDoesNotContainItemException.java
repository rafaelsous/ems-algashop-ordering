package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;

public class ShoppingCartDoesNotContainItemException extends DomainException {

    public ShoppingCartDoesNotContainItemException(ShoppingCartId id, ShoppingCartItemId shoppingCartItemId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM.formatted(id, shoppingCartItemId));
    }
}