package com.rafaelsousa.algashop.ordering.domain.model.exception;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;

public class ProductOutOfStockException extends DomainException {
    public ProductOutOfStockException(ProductId id) {
        super(ErrorMessages.ERROR_PRODUCT_OUT_OF_STOCK.formatted(id));
    }
}