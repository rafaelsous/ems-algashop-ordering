package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.customer;

import static org.assertj.core.api.Assertions.assertThat;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.utils.AlgaShopResourceUtils;
import com.rafaelsousa.algashop.ordering.utils.MockJwtFactory;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

class MyCustomerControllerIT extends AbstractPresentationIT {
    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @Autowired private CustomerPersistenceRepository customerPersistenceRepository;

    @BeforeEach
    void setUp() {
        super.beforeEach();
    }

    @Test
    void shouldCreateMyCustomerProfile() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        UUID createdCustomerId =
                givenAuthenticatedAltCustomer()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(json)
                    .when()
                        .post("/api/v1/customers/me")
                    .then()
                        .assertThat()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .statusCode(HttpStatus.CREATED.value())
                        .header("Location", Matchers.containsString("/api/v1/customers/me"))
                        .body("id", Matchers.is(MockJwtFactory.ALT_CUSTOMER_SUBJECT))
                        .extract()
                        .jsonPath()
                        .getUUID("id");

        boolean customerExists = customerPersistenceRepository.existsById(createdCustomerId);
        assertThat(customerExists).isTrue();
    }

    @Test
    void shouldLoadMyCustomerProfile() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers/me")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.is(VALID_CUSTOMER_ID.toString()));
    }

    @Test
    void shouldUpdateMyCustomerProfile() {
        givenAuthenticatedRequest()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(AlgaShopResourceUtils.readContent("json/update-customer.json"))
        .when()
            .put("/api/v1/customers/me")
        .then()
            .assertThat()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .statusCode(HttpStatus.OK.value())
            .body("id", Matchers.is(VALID_CUSTOMER_ID.toString()));

        CustomerPersistence updatedCustomerProfile = customerPersistenceRepository.findById(VALID_CUSTOMER_ID)
            .orElseThrow();

        assertThat(updatedCustomerProfile).satisfies(customer -> {
            assertThat(customer.getFirstName()).isEqualTo("Silvester");
            assertThat(customer.getLastName()).isEqualTo("Stalone");
        });
    }

    @Test
    void shouldNotCreateCustomerWithInvalidData() {
        String json =
                AlgaShopResourceUtils.readContent("json/create-customer-with-invalid-data.json");

        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenHasExpired() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        givenAuthenticatedWithExpiredTokenRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/customers/me")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnForbidenWhenCreatingMyCustomerProfileWithoutScope() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        givenAuthenticatedWithNoScopeTokenRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/customers/me")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
