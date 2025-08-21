package com.rafaelsousa.algashop.ordering.infrastructure.fake;

import com.rafaelsousa.algashop.ordering.domain.model.service.ShippingCostService;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ShippingCostServiceFakeImpl implements ShippingCostService {

    @Override
    public CalculationResponse calculate(CalculationRequest request) {
        return CalculationResponse.builder()
                .cost(Money.of("19.90"))
                .expectedDeliveryDate(LocalDate.now().plusDays(7))
                .build();
    }
}