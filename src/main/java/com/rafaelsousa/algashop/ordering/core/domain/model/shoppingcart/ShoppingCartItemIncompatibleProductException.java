package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;

public class ShoppingCartItemIncompatibleProductException extends DomainException {

    public ShoppingCartItemIncompatibleProductException(ShoppingCartItemId shoppingCartItemId, ProductId productId) {
        super(ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT.formatted(shoppingCartItemId, productId));
    }
}