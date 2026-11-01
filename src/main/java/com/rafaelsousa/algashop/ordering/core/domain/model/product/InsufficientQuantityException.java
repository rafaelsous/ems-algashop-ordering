package com.rafaelsousa.algashop.ordering.core.domain.model.product;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Quantity;

public class InsufficientQuantityException extends DomainException {

    public InsufficientQuantityException(ProductName name, Quantity requestedQuantity) {
        super(ErrorMessages.ERROR_PRODUCT_INSUFFICIENT_STOCK.formatted(name.value(), requestedQuantity.value()));
    }
}
