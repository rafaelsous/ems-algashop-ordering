package com.rafaelsousa.algashop.ordering.core.domain.model.product;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;

public class ProductOutOfStockException extends DomainException {
    public ProductOutOfStockException(ProductId productId) {
        super(ErrorMessages.ERROR_PRODUCT_OUT_OF_STOCK.formatted(productId.value()));
    }
}