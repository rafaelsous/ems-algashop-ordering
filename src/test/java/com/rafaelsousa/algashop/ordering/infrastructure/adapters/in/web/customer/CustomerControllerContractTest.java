package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.customer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rafaelsousa.algashop.ordering.core.application.customer.CustomerOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.core.application.customer.CustomerSummaryOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerFilter;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerSummaryOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForQueryingCustomers;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ForQueryingShoppingCarts;

import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartOutput;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerContractTest {
    private final WebApplicationContext webApplicationContext;

    @Autowired
    CustomerControllerContractTest(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @MockitoBean private ForQueryingCustomers customerQueryService;

    @MockitoBean private ForQueryingShoppingCarts shoppingCartQueryService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                        .build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void filterCustomersContract() {
        int pageNumber = 0;
        int pageLimit = 5;

        CustomerSummaryOutput customer1 = CustomerSummaryOutputTestDataBuilder.existing().build();
        CustomerSummaryOutput customer2 =
                CustomerSummaryOutputTestDataBuilder.existingAlt1().build();

        when(customerQueryService.filter(any(CustomerFilter.class)))
                .thenReturn(new PageImpl<>(List.of(customer1, customer2)));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("page", pageNumber)
                .queryParam("size", pageLimit)
                .when()
                .get("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "number", Matchers.equalTo(pageNumber),
                        "size", Matchers.equalTo(2),
                        "totalPages", Matchers.equalTo(1),
                        "totalElements", Matchers.equalTo(2),
                        "content", Matchers.hasSize(2),
                        "content[0].id", Matchers.is(customer1.getId().toString()),
                        "content[0].firstName", Matchers.is(customer1.getFirstName()),
                        "content[0].lastName", Matchers.is(customer1.getLastName()),
                        "content[0].email", Matchers.is(customer1.getEmail()),
                        "content[0].document", Matchers.is(customer1.getDocument()),
                        "content[0].phone", Matchers.is(customer1.getPhone()),
                        "content[0].birthDate", Matchers.is(customer1.getBirthDate().toString()),
                        "content[0].registeredAt",
                                Matchers.is(formatter.format(customer1.getRegisteredAt())),
                        "content[0].archived", Matchers.is(customer1.getArchived()),
                        "content[0].archivedAt", Matchers.nullValue(),
                        "content[0].loyaltyPoints", Matchers.is(customer1.getLoyaltyPoints()),
                        "content[0].promotionNotificationsAllowed",
                                Matchers.is(customer1.getPromotionNotificationsAllowed()),
                        "content[1].id", Matchers.is(customer2.getId().toString()),
                        "content[1].firstName", Matchers.is(customer2.getFirstName()),
                        "content[1].lastName", Matchers.is(customer2.getLastName()),
                        "content[1].email", Matchers.is(customer2.getEmail()),
                        "content[1].document", Matchers.is(customer2.getDocument()),
                        "content[1].phone", Matchers.is(customer2.getPhone()),
                        "content[1].birthDate", Matchers.is(customer2.getBirthDate().toString()),
                        "content[1].registeredAt",
                                Matchers.is(formatter.format(customer2.getRegisteredAt())),
                        "content[1].archived", Matchers.is(customer2.getArchived()),
                        "content[1].archivedAt", Matchers.nullValue(),
                        "content[1].loyaltyPoints", Matchers.is(customer2.getLoyaltyPoints()),
                        "content[1].promotionNotificationsAllowed",
                                Matchers.is(customer2.getPromotionNotificationsAllowed()));
    }

    @Test
    void findByIdContract() {
        CustomerOutput customer = CustomerOutputTestDataBuilder.existing().build();

        when(customerQueryService.findById(customer.getId())).thenReturn(customer);

        AddressData address = customer.getAddress();
        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/{customerId}", customer.getId())
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "id", Matchers.is(customer.getId().toString()),
                        "firstName", Matchers.is(customer.getFirstName()),
                        "lastName", Matchers.is(customer.getLastName()),
                        "email", Matchers.is(customer.getEmail()),
                        "document", Matchers.is(customer.getDocument()),
                        "phone", Matchers.is(customer.getPhone()),
                        "birthDate", Matchers.is(customer.getBirthDate().toString()),
                        "promotionNotificationsAllowed",
                                Matchers.is(customer.getPromotionNotificationsAllowed()),
                        "registeredAt", Matchers.notNullValue(),
                        "archived", Matchers.is(false),
                        "loyaltyPoints", Matchers.is(0),
                        "address.street", Matchers.is(address.getStreet()),
                        "address.number", Matchers.is(address.getNumber()),
                        "address.complement", Matchers.is(address.getComplement()),
                        "address.neighborhood", Matchers.is(address.getNeighborhood()),
                        "address.city", Matchers.is(address.getCity()),
                        "address.state", Matchers.is(address.getState()),
                        "address.zipCode", Matchers.is(address.getZipCode()));
    }

    @Test
    void findByIdError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        when(customerQueryService.findById(invalidCustomerId))
                .thenThrow(CustomerNotFoundException.class);

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/{customerId}", invalidCustomerId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(
                        "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
                        "type", Matchers.is("/errors/not-found"),
                        "title", Matchers.is("Not found"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue());
    }

    @Test
    void findShoppingCartByCustomerIdContract() {
        UUID customerId = UUID.randomUUID();
        UUID shoppingCartId = UUID.randomUUID();

        when(shoppingCartQueryService.findByCustomerId(customerId))
            .thenReturn(ShoppingCartOutput.builder()
                .id(shoppingCartId)
                .customerId(customerId)
                .totalItems(0)
                .totalAmount(BigDecimal.ZERO)
                .build());

        RestAssuredMockMvc.given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/v1/customers/{customerId}/shopping-cart", customerId)
            .then()
            .assertThat()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .statusCode(HttpStatus.OK.value())
            .body(
                "id", Matchers.is(shoppingCartId.toString()),
                "customerId", Matchers.is(customerId.toString()),
                "totalItems", Matchers.is(0),
                "totalAmount", Matchers.is(0)
            );
    }
}
