package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;

public interface ShoppingCartProductAdjustmentService {
    void adjustPrice(ProductId productId, Money price);
    void changeAvailability(ProductId productId, boolean available);
}