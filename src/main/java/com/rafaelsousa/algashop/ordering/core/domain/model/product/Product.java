package com.rafaelsousa.algashop.ordering.core.domain.model.product;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Quantity;
import lombok.Builder;

import java.util.Objects;

public record Product(
        ProductId id,
        ProductName name,
        Money price,
        Boolean inStock,
        Quantity quantityInStock
) {
    public Product {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(price);
        Objects.requireNonNull(inStock);
    }

    @Builder
    public static Product of(ProductId id, ProductName name, Money price, Boolean inStock, Quantity quantityInStock) {
        if (Objects.isNull(inStock)) {
            inStock = true;
        }

        return new Product(id, name, price, inStock, quantityInStock);
    }

    public void checkOutOfStock() {
        if (isOutOfStock()) {
            throw new ProductOutOfStockException(this.id());
        }
    }

    public void checkForQuantity(Quantity requestedQuantity) {
        Objects.requireNonNull(requestedQuantity);
        checkOutOfStock();

        if (this.quantityInStock != null && this.quantityInStock().compareTo(requestedQuantity) < 0) {
            throw new InsufficientQuantityException(this.name(), requestedQuantity);
        }
    }

    private boolean isOutOfStock() {
        return !inStock();
    }
}