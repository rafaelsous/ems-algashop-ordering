package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;

public class CantAddLoyaltyPointsOrderIsNotReady extends DomainException {

    public CantAddLoyaltyPointsOrderIsNotReady(OrderId orderId) {
        super(ErrorMessages.ERROR_CANNOT_ADD_LOYALTY_POINTS_ORDER_IS_NOT_READY.formatted(orderId.value()));
    }
}