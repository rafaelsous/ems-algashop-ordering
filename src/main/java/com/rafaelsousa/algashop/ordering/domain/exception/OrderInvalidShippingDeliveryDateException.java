package com.rafaelsousa.algashop.ordering.domain.exception;

import com.rafaelsousa.algashop.ordering.domain.valueobject.id.OrderId;

public class OrderInvalidShippingDeliveryDateException extends DomainException {
    public OrderInvalidShippingDeliveryDateException(OrderId id) {
        super(ErrorMessages.ERROR_ORDER_DELIVERY_DATE_CANNOT_IN_THE_PAST.formatted(id));
    }
}