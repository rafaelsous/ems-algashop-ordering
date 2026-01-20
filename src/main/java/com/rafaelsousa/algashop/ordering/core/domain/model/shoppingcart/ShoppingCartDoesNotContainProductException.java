package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;

public class ShoppingCartDoesNotContainProductException extends DomainException {

    public ShoppingCartDoesNotContainProductException(ShoppingCartId id, ProductId productId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT.formatted(id, productId));
    }
}