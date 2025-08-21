package com.rafaelsousa.algashop.ordering.domain.model.product;

import com.rafaelsousa.algashop.ordering.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;

public class ProductOutOfStockException extends DomainException {
    public ProductOutOfStockException(ProductId id) {
        super(ErrorMessages.ERROR_PRODUCT_OUT_OF_STOCK.formatted(id));
    }
}