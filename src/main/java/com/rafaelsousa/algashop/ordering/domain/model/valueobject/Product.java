package com.rafaelsousa.algashop.ordering.domain.model.valueobject;

import com.rafaelsousa.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;
import lombok.Builder;

import java.util.Objects;

public record Product(
        ProductId id,
        ProductName name,
        Money price,
        Boolean inStock
) {
    public Product {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(price);
        Objects.requireNonNull(inStock);
    }

    @Builder
    public static Product of(ProductId id, ProductName name, Money price, Boolean inStock) {
        if (Objects.isNull(inStock)) {
            inStock = true;
        }

        return new Product(id, name, price, inStock);
    }

    public void checkOutOfStock() {
        if (isOutOfStock()) {
            throw new ProductOutOfStockException(this.id());
        }
    }

    private boolean isOutOfStock() {
        return !inStock();
    }
}