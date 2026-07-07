package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.customer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rafaelsousa.algashop.ordering.core.application.customer.CustomerManagementApplicationService;
import com.rafaelsousa.algashop.ordering.core.application.customer.CustomerOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerEmailAlreadyExistsException;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForQueryingCustomers;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ForQueryingShoppingCarts;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@WebMvcTest(controllers = MyCustomerController.class)
class MyCustomerControllerContractTest {
    private static final UUID AUTHENTICATED_CUSTOMER_ID =
            UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    private final WebApplicationContext webApplicationContext;

    @Autowired
    MyCustomerControllerContractTest(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @MockitoBean private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoBean private ForQueryingCustomers customerQueryService;

    @MockitoBean private ForQueryingShoppingCarts shoppingCartQueryService;

    @MockitoBean private SecurityChecks securityChecks;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                        .build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        when(securityChecks.getAuthenticatedUserId()).thenReturn(AUTHENTICATED_CUSTOMER_ID);
    }

    @Test
    void createMyCustomerProfileContract() {
        CustomerOutput customerOutput =
                CustomerOutputTestDataBuilder.existing().id(AUTHENTICATED_CUSTOMER_ID).build();

        when(customerManagementApplicationService.create(
                        Mockito.eq(AUTHENTICATED_CUSTOMER_ID), any(CustomerInput.class)))
                .thenReturn(AUTHENTICATED_CUSTOMER_ID);
        when(customerQueryService.findById(AUTHENTICATED_CUSTOMER_ID)).thenReturn(customerOutput);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@example.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1990-01-01",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .post("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, Matchers.containsString("/api/v1/customers/me"))
                .body(
                        "id", Matchers.notNullValue(),
                        "firstName", Matchers.is("John"),
                        "lastName", Matchers.is("Doe"),
                        "email", Matchers.is("johndoe@example.com"),
                        "document", Matchers.is("12345"),
                        "phone", Matchers.is("1191234564"),
                        "birthDate", Matchers.is("1990-01-01"),
                        "promotionNotificationsAllowed", Matchers.is(false),
                        "registeredAt", Matchers.notNullValue(),
                        "archived", Matchers.is(false),
                        "loyaltyPoints", Matchers.is(0),
                        "address.street", Matchers.is("123 Main St"),
                        "address.number", Matchers.is("100"),
                        "address.complement", Matchers.is("Apt 4B"),
                        "address.neighborhood", Matchers.is("Downtown"),
                        "address.city", Matchers.is("Springfield"),
                        "address.state", Matchers.is("South Carolina"),
                        "address.zipCode", Matchers.is("62701"));
    }

    @Test
    void createMyCustomerProfileError400Contract() {
        String inputJson =
                """
                {
                  "firstName": "",
                  "lastName": "",
                  "email": "johndoe@example.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1990-01-01",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .post("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(
                        "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
                        "type", Matchers.is("/errors/invalid-fields"),
                        "title", Matchers.is("Invalid fields"),
                        "instance", Matchers.notNullValue(),
                        "detail", Matchers.is("One or more fields are invalid"),
                        "timestamp", Matchers.notNullValue(),
                        "fields", Matchers.notNullValue(),
                        "fields.size()", Matchers.is(2),
                        "fields.firstName", Matchers.is("must not be blank"),
                        "fields.lastName", Matchers.is("must not be blank"));
    }

    @Test
    void createMyCustomerProfileError409Contract() {
        when(customerManagementApplicationService.create(any(UUID.class), any(CustomerInput.class)))
                .thenThrow(CustomerEmailAlreadyExistsException.class);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@example.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1990-01-01",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .post("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(
                        "status", Matchers.is(HttpStatus.CONFLICT.value()),
                        "type", Matchers.is("/errors/conflict"),
                        "title", Matchers.is("Conflict"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue());
    }

    @Test
    void createMyCustomerProfileError422Contract() {
        when(customerManagementApplicationService.create(any(UUID.class), any(CustomerInput.class)))
                .thenThrow(DomainException.class);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@example.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1990-01-01",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .post("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value())
                .body(
                        "status", Matchers.is(HttpStatus.UNPROCESSABLE_CONTENT.value()),
                        "type", Matchers.is("/errors/unprocessable-entity"),
                        "title", Matchers.is("Unprocessable entity"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue());
    }

    @Test
    void createMyCustomerProfileError500Contract() {
        when(customerManagementApplicationService.create(any(UUID.class), any(CustomerInput.class)))
                .thenThrow(RuntimeException.class);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@example.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1990-01-01",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .post("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(
                        "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "type", Matchers.is("/errors/internal"),
                        "title", Matchers.is("Internal server error"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue());
    }

    @Test
    void loadMyCustomerProfileContract() {
        CustomerOutput customer =
                CustomerOutputTestDataBuilder.existing().id(AUTHENTICATED_CUSTOMER_ID).build();

        when(customerQueryService.findById(customer.getId())).thenReturn(customer);

        AddressData address = customer.getAddress();
        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/me")
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
    void loadMyCustomerProfileError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        when(securityChecks.getAuthenticatedUserId()).thenReturn(invalidCustomerId);

        when(customerQueryService.findById(invalidCustomerId))
                .thenThrow(CustomerNotFoundException.class);

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/me")
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
    void updateMyCustomerProfileContract() {
        CustomerOutput customerOutput =
                CustomerOutputTestDataBuilder.existing().id(AUTHENTICATED_CUSTOMER_ID).build();
        AddressData address = customerOutput.getAddress();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        when(customerQueryService.findById(customerOutput.getId())).thenReturn(customerOutput);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "phone": "1191234564",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "id", Matchers.is(customerOutput.getId().toString()),
                        "firstName", Matchers.is(customerOutput.getFirstName()),
                        "lastName", Matchers.is(customerOutput.getLastName()),
                        "email", Matchers.is(customerOutput.getEmail()),
                        "document", Matchers.is(customerOutput.getDocument()),
                        "phone", Matchers.is(customerOutput.getPhone()),
                        "birthDate", Matchers.is(customerOutput.getBirthDate().toString()),
                        "promotionNotificationsAllowed",
                                Matchers.is(customerOutput.getPromotionNotificationsAllowed()),
                        "registeredAt",
                                Matchers.is(formatter.format(customerOutput.getRegisteredAt())),
                        "archived", Matchers.is(customerOutput.getArchived()),
                        "loyaltyPoints", Matchers.is(customerOutput.getLoyaltyPoints()),
                        "address.street", Matchers.is(address.getStreet()),
                        "address.number", Matchers.is(address.getNumber()),
                        "address.complement", Matchers.is(address.getComplement()),
                        "address.neighborhood", Matchers.is(address.getNeighborhood()),
                        "address.city", Matchers.is(address.getCity()),
                        "address.state", Matchers.is(address.getState()),
                        "address.zipCode", Matchers.is(address.getZipCode()));
    }

    @Test
    void updateMyCustomerProfileError400Contract() {
        String inputJson =
                """
                {
                  "firstName": "",
                  "lastName": "",
                  "phone": "1191234564",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(
                        "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
                        "type", Matchers.is("/errors/invalid-fields"),
                        "title", Matchers.is("Invalid fields"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue(),
                        "fields", Matchers.notNullValue(),
                        "fields.size()", Matchers.is(2),
                        "fields.firstName", Matchers.is("must not be blank"),
                        "fields.lastName", Matchers.is("must not be blank"));
    }

    @Test
    void updateMyCustomerProfileError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        when(securityChecks.getAuthenticatedUserId()).thenReturn(invalidCustomerId);

        when(customerQueryService.findById(invalidCustomerId))
                .thenThrow(CustomerNotFoundException.class);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "phone": "1191234564",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/me")
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
    void updateMyCustomerProfileError409Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        when(securityChecks.getAuthenticatedUserId()).thenReturn(invalidCustomerId);

        when(customerQueryService.findById(invalidCustomerId))
                .thenThrow(CustomerEmailAlreadyExistsException.class);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "phone": "1191234564",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(
                        "status", Matchers.is(HttpStatus.CONFLICT.value()),
                        "type", Matchers.is("/errors/conflict"),
                        "title", Matchers.is("Conflict"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue());
    }

    @Test
    void updateMyCustomerProfileError422Contract() {
        when(securityChecks.getAuthenticatedUserId()).thenReturn(AUTHENTICATED_CUSTOMER_ID);

        when(customerQueryService.findById(AUTHENTICATED_CUSTOMER_ID))
                .thenThrow(DomainException.class);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "phone": "1191234564",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value())
                .body(
                        "status", Matchers.is(HttpStatus.UNPROCESSABLE_CONTENT.value()),
                        "type", Matchers.is("/errors/unprocessable-entity"),
                        "title", Matchers.is("Unprocessable entity"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue());
    }

    @Test
    void updateMyCustomerProfileError500Contract() {
        when(securityChecks.getAuthenticatedUserId()).thenReturn(AUTHENTICATED_CUSTOMER_ID);

        when(customerQueryService.findById(AUTHENTICATED_CUSTOMER_ID))
                .thenThrow(RuntimeException.class);

        String inputJson =
                """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "phone": "1191234564",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "123 Main St",
                    "number": "100",
                    "complement": "Apt 4B",
                    "neighborhood": "Downtown",
                    "city": "Springfield",
                    "state": "South Carolina",
                    "zipCode": "62701"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .put("/api/v1/customers/me")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(
                        "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "type", Matchers.is("/errors/internal"),
                        "title", Matchers.is("Internal server error"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue());
    }
}
