package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;

import static com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class ShoppingCartTestDataBuilder {
    public static final ShoppingCartId DEFAULT_SHOPPING_CART_ID = new ShoppingCartId();

    private boolean withItems = true;
    private CustomerId customerId = DEFAULT_CUSTOMER_ID;

    private ShoppingCartTestDataBuilder() {}

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCart build() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);

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

    public ShoppingCartTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }
}