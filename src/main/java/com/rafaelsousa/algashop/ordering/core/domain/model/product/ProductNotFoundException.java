package com.rafaelsousa.algashop.ordering.core.domain.model.product;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;

public class ProductNotFoundException extends DomainEntityNotFoundException {
    public ProductNotFoundException(ProductId productId) {
        super(ErrorMessages.ERROR_PRODUCT_NOT_FOUND.formatted(productId.value()));
    }
}