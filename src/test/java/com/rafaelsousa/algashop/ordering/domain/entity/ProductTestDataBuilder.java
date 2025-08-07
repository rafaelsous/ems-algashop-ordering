package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ProductId;

public class ProductTestDataBuilder {

    private ProductTestDataBuilder () { }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
                .id(new ProductId())
                .name(ProductName.of("Macbook M4"))
                .price(Money.of("15000"))
                .inStock(true);
    }

    public static Product.ProductBuilder aProductUnavailable() {
        return Product.builder()
                .id(new ProductId())
                .name(ProductName.of("Monitor 31 inch"))
                .price(Money.of("2500.00"))
                .inStock(false);
    }

    public static Product.ProductBuilder aProductAltRamMemory() {
        return Product.builder()
                .id(new ProductId())
                .name(ProductName.of("4G RAM"))
                .price(Money.of("150.00"))
                .inStock(true);
    }

    public static Product.ProductBuilder aProductAltMousePad() {
        return Product.builder()
                .id(new ProductId())
                .name(ProductName.of("Mouse pad"))
                .price(Money.of("100.00"))
                .inStock(true);
    }
}