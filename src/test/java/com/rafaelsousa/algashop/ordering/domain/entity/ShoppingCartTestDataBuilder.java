package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.CustomerId;

public class ShoppingCartTestDataBuilder {
    private boolean withItems = true;

    private ShoppingCartTestDataBuilder() {}

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCart build() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(new CustomerId());

        if (this.withItems) {
            Product mousePad = ProductTestDataBuilder.aProductAltMousePad().build();
            Product ramMemory = ProductTestDataBuilder.aProductAltRamMemory().build();

            shoppingCart.addItem(mousePad, Quantity.of(1));
            shoppingCart.addItem(ramMemory, Quantity.of(1));
        }

        return shoppingCart;
    }

    public ShoppingCartTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }
}