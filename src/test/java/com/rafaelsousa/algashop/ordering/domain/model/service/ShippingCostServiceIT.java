package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.service.ShippingCostService.CalculationRequest;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.ZipCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShippingCostServiceIT {
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;

    @Autowired
    ShippingCostServiceIT(ShippingCostService shippingCostService, OriginAddressService originAddressService) {
        this.shippingCostService = shippingCostService;
        this.originAddressService = originAddressService;
    }

    @Test
    void shouldCalculate() {
        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = ZipCode.of("12345");

        CalculationRequest request = CalculationRequest.builder()
                .origin(origin)
                .destination(destination)
                .build();

        ShippingCostService.CalculationResponse response = shippingCostService.calculate(request);

        assertThat(response).satisfies(
                r -> assertThat(r.cost()).isNotNull(),
                r -> assertThat(r.expectedDeliveryDate()).isNotNull()
        );
    }
}