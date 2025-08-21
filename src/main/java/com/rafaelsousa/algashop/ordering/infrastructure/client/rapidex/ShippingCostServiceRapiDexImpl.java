package com.rafaelsousa.algashop.ordering.infrastructure.client.rapidex;

import com.rafaelsousa.algashop.ordering.domain.model.service.ShippingCostService;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
public class ShippingCostServiceRapiDexImpl implements ShippingCostService {
    private final RapiDexApiClient rapiDexApiClient;

    @Override
    public CalculationResponse calculate(CalculationRequest request) {
        DeliveryCostResponse deliveryCostResponse = rapiDexApiClient.calculate(DeliveryCostRequest.builder()
                .originZipCode(request.origin().value())
                .destinationZipCode(request.destination().value())
                .build());

        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(deliveryCostResponse.estimatedDaysToDeliver());

        return CalculationResponse.builder()
                .cost(Money.of(deliveryCostResponse.deliveryCost()))
                .expectedDeliveryDate(expectedDeliveryDate)
                .build();
    }
}