package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.shipping.client.fake;

import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Money;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "FAKE")
public class ShippingCostServiceFakeImpl implements ShippingCostService {

    @Override
    public CalculationResponse calculate(CalculationRequest request) {
        return CalculationResponse.builder()
                .cost(Money.of("19.90"))
                .expectedDeliveryDate(LocalDate.now().plusDays(7))
                .build();
    }
}