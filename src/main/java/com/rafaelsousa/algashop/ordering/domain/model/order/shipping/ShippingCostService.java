package com.rafaelsousa.algashop.ordering.domain.model.order.shipping;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.ZipCode;
import lombok.Builder;

import java.time.LocalDate;

public interface ShippingCostService {
    CalculationResponse calculate(CalculationRequest request);

    @Builder
    record CalculationRequest(ZipCode origin, ZipCode destination) {}

    @Builder
    record CalculationResponse(Money cost, LocalDate expectedDeliveryDate) {}
}