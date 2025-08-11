package com.rafaelsousa.algashop.ordering.domain.exception;

import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ProductId;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;

public class ShoppingCartItemIncompatibleProductException extends DomainException {

    public ShoppingCartItemIncompatibleProductException(ShoppingCartItemId shoppingCartItemId, ProductId productId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT.formatted(shoppingCartItemId, productId));
    }
}