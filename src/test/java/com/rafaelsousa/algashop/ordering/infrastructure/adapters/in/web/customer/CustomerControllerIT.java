package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.customer;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.rafaelsousa.algashop.ordering.utils.AlgaShopResourceUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerControllerIT extends AbstractPresentationIT {

    @Autowired
    private CustomerPersistenceRepository customerPersistenceRepository;

    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");
    private static final UUID INVALID_CUSTOMER_ID = UUID.fromString("019b7504-4e4f-741c-99a7-84d3c651ea19");

    @BeforeEach
    void setUp() {
        super.beforeEach();
    }

    @Test
    void shouldCreateCustomer() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        String createdCustomerId = givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body(
                        "id", Matchers.not(Matchers.emptyString())
                )
            .extract()
                .jsonPath().getString("id");

        boolean customerExists = customerPersistenceRepository.existsById(UUID.fromString(createdCustomerId));
        assertThat(customerExists).isTrue();
    }

    @Test
    void shouldNotCreateCustomerWithInvalidData() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer-with-invalid-data.json");

	    givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldArchiveCustomer() {
	    givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .delete(URI.create("api/v1/customers/" + VALID_CUSTOMER_ID))
            .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(customerPersistenceRepository.existsById(VALID_CUSTOMER_ID)).isTrue();
        assertThat(customerPersistenceRepository.findById(VALID_CUSTOMER_ID).orElseThrow().getArchived()).isTrue();
        assertThat(customerPersistenceRepository.findById(VALID_CUSTOMER_ID).orElseThrow().getArchivedAt()).isNotNull();
    }

    @Test
    void shouldNotArchiveInexistentCustomer() {
	    givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .delete(URI.create("api/v1/customers/" + INVALID_CUSTOMER_ID))
            .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}