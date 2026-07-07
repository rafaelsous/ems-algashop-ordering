package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order;

import com.rafaelsousa.algashop.ordering.core.domain.model.IdGenerator;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class OrderControllerIT extends AbstractPresentationIT {

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
    void shouldFindOrderByIdWhenAuthenticatedAsManager() {
        String orderId = "00001J8JEEVR1";

        givenAuthenticatedManagerRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.is(orderId));
    }

    @Test
    void shouldNotFoundOrderWhenOrderNotExists() {
        String inexistentOrderId = IdGenerator.generateTSID().toString();

        givenAuthenticatedManagerRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/orders/{orderId}", inexistentOrderId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldListOrdersWhenAuthenticatedAsManager() {
        givenAuthenticatedManagerRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotListAdministrativeOrdersWhenAuthenticatedAsCustomer() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
