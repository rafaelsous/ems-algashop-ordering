package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductName;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductId;

public class ShoppingCartItemTestDataBuilder {
    public ShoppingCartItemId id = new ShoppingCartItemId();
    public Money price = Money.of("1000.00");
    public Quantity quantity = Quantity.of(1);

    private ShoppingCartItemTestDataBuilder() {
    }

    public static ShoppingCartItemTestDataBuilder aShoppingCartItem() {
        return new ShoppingCartItemTestDataBuilder();
    }

    public ShoppingCartItem build() {
        return ShoppingCartItem.brandNew()
                .shoppingCartId(new ShoppingCartId())
                .product(Product.builder()
                        .id(new ProductId())
                        .name(new ProductName("Notebook"))
                        .price(price)
                        .inStock(true)
                        .build())
                .quantity(Quantity.of(1))
                .build();
    }

    public ShoppingCartItemTestDataBuilder id(ShoppingCartItemId id) {
        this.id = id;
        return this;
    }

    public ShoppingCartItemTestDataBuilder price(Money price) {
        this.price = price;
        return this;
    }

    public ShoppingCartItemTestDataBuilder quantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }
}