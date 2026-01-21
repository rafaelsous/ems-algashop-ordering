package com.rafaelsousa.algashop.ordering.presentation.order;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.OrderPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceRepository;
import com.rafaelsousa.algashop.ordering.presentation.AbstractPresentationIT;
import com.rafaelsousa.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

class OrderControllerWithoutProductCatalogIT extends AbstractPresentationIT {

    @Autowired
    private CustomerPersistenceRepository customerPersistenceRepository;

    @Autowired
    private OrderPersistenceRepository orderPersistenceRepository;

    @Autowired
    private ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;

    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d");
    private static final UUID VALID_SHOPPING_CART_ID = UUID.fromString("28fcd9fb-4ce7-44d6-9583-14d8b3dc5aff");

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

        wireMockProductCatalog.stop();

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.GATEWAY_TIMEOUT.value());
    }
}
