package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.rafaelsousa.algashop.ordering.utils.AlgaShopResourceUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
		"algashop.integrations.product-catalog.url=http://localhost:9999"
})
class OrderControllerWithoutProductCatalogIT extends AbstractPresentationIT {

    @BeforeEach
    void setUp() {
        super.beforeEach();
    }

    @BeforeAll
    static void beforeAll() {
        initWireMock();
    }

    @AfterAll
    static void afterAll() {
        stopWireMock();
    }

    @Test
    void shouldNotCreateOrderWithProductWhenProductApiIsUnavailable() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");

	    givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/customers/me/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.GATEWAY_TIMEOUT.value());
    }
}
