package com.rafaelsousa.algashop.ordering.domain.model.order.shipping;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.rafaelsousa.algashop.ordering.domain.model.commons.ZipCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
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

    private WireMockServer wireMockRapidex;

    @BeforeEach
    void setUp() {
        wireMockRapidex = new WireMockServer(options()
                .port(8680)
                .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex")
                .extensions(new ResponseTemplateTransformer(true)));

        wireMockRapidex.start();
    }

    @AfterEach
    void after() {
        wireMockRapidex.stop();
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
                r -> assertThat(r.cost()).isEqualByComparingTo(Money.of("35.00")),
                r -> assertThat(r.expectedDeliveryDate()).isEqualTo(LocalDate.now().plusDays(7))
        );
    }
}