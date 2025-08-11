package com.rafaelsousa.algashop.ordering.domain.model.entity;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

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