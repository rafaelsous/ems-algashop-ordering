package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.customer;


import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

class CustomerControllerIT extends AbstractPresentationIT {
    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @BeforeEach
    void setUp() {
        super.beforeEach();
    }

    @Test
    void shouldFilterCustomerWhenAuthenticatedAsManager() {
        givenAuthenticatedManagerRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnForbiddenWhenFilterCustomersAsCustomer() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnCustomerWhenFindByIdAsManager() {
        givenAuthenticatedManagerRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers/{customerId}", VALID_CUSTOMER_ID)
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.is(VALID_CUSTOMER_ID.toString()));
    }

    @Test
    void shouldReturnCustomerShoppingCartWhenFindAsManager() {
        givenAuthenticatedManagerRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers/{customerId}/shopping-cart", VALID_CUSTOMER_ID)
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("customerId", Matchers.is(VALID_CUSTOMER_ID.toString()));
    }
}
