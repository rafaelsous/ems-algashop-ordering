package com.rafaelsousa.algashop.ordering.domain.model.entity;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;

public class ProductTestDataBuilder {
    private static final ProductId PRODUCT_ID = new ProductId();
    private static final ProductId UNAVAILABLE_PRODUCT_ID = new ProductId();
    private static final ProductId ALT_RAM_MEMORY_PRODUCT_ID = new ProductId();
    private static final ProductId ALT_MOUSE_PAD_PRODUCT_ID = new ProductId();

    private ProductTestDataBuilder () { }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
                .id(PRODUCT_ID)
                .name(ProductName.of("Macbook M4"))
                .price(Money.of("15000.00"));
    }

    public static Product.ProductBuilder aProductUnavailable() {
        return Product.builder()
                .id(UNAVAILABLE_PRODUCT_ID)
                .name(ProductName.of("Monitor 31 inch"))
                .price(Money.of("2500.00"))
                .inStock(false);
    }

    public static Product.ProductBuilder aProductAltRamMemory() {
        return Product.builder()
                .id(ALT_RAM_MEMORY_PRODUCT_ID)
                .name(ProductName.of("4G RAM"))
                .price(Money.of("150.00"));
    }

    public static Product.ProductBuilder aProductAltMousePad() {
        return Product.builder()
                .id(ALT_MOUSE_PAD_PRODUCT_ID)
                .name(ProductName.of("Mouse pad"))
                .price(Money.of("100.00"));
    }
}