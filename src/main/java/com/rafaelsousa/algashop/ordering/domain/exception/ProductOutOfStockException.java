package com.rafaelsousa.algashop.ordering.domain.exception;

import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ProductId;

public class ProductOutOfStockException extends DomainException {
    public ProductOutOfStockException(ProductId id) {
        super(ErrorMessages.ERROR_PRODUCT_OUT_OF_STOCK.formatted(id));
    }
}