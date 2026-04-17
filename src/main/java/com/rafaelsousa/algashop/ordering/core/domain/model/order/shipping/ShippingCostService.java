package com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.ZipCode;
import lombok.Builder;

import java.time.LocalDate;

public interface ShippingCostService {
    CalculationResponse calculate(CalculationRequest request);

    @Builder
    record CalculationRequest(ZipCode origin, ZipCode destination) {}

    @Builder
    record CalculationResponse(Money cost, LocalDate expectedDate) {}
}