package com.rafaelsousa.algashop.ordering.presentation;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;
import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerInput;
import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.rafaelsousa.algashop.ordering.application.customer.query.*;
import com.rafaelsousa.algashop.ordering.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerEmailAlreadyExistsException;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerContractTest {
    private final WebApplicationContext webApplicationContext;

    @Autowired
    CustomerControllerContractTest(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @MockitoBean
    private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoBean
    private CustomerQueryService customerQueryService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void createCustomerContract() {
        CustomerOutput customerOutput = CustomerOutputTestDataBuilder.existing().build();

        UUID customerId = UUID.randomUUID();
        when(customerManagementApplicationService.create(any(CustomerInput.class)))
                .thenReturn(customerId);
        when(customerQueryService.findById(Mockito.any(UUID.class)))
                .thenReturn(customerOutput);

        String inputJson = """
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

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, Matchers.containsString("/api/v1/customers/" + customerId))
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
                        "address.zipCode", Matchers.is("62701")
                );
    }

    @Test
    void createCustomerError400Contract() {
        String inputJson = """
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
                .post("/api/v1/customers")
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
                        "fields.lastName", Matchers.is("must not be blank")
                );
    }

    @Test
    void createCustomerError409Contract() {
        when(customerManagementApplicationService.create(any(CustomerInput.class)))
                .thenThrow(CustomerEmailAlreadyExistsException.class);

        String inputJson = """
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

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
            .when()
                .post("/api/v1/customers")
            .then()
            .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(
                        "status", Matchers.is(HttpStatus.CONFLICT.value()),
                        "type", Matchers.is("/errors/conflict"),
                        "title", Matchers.is("Conflict"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue()
                );
    }

    @Test
    void createCustomerError422Contract() {
        when(customerManagementApplicationService.create(any(CustomerInput.class)))
                .thenThrow(DomainException.class);

        String inputJson = """
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

        RestAssuredMockMvc
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body(
                        "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                        "type", Matchers.is("/errors/unprocessable-entity"),
                        "title", Matchers.is("Unprocessable entity"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue()
                );
    }

    @Test
    void createCustomerError500Contract() {
        when(customerManagementApplicationService.create(any(CustomerInput.class)))
                .thenThrow(RuntimeException.class);

        String inputJson = """
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

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(inputJson)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(
                        "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "type", Matchers.is("/errors/internal"),
                        "title", Matchers.is("Internal server error"),
                        "instance", Matchers.notNullValue(),
                        "timestamp", Matchers.notNullValue()
                );
    }

    @Test
    void findCustomersContract() {
        int pageNumber = 0;
        int pageLimit = 5;

        CustomerSummaryOutput customer1 = CustomerSummaryOutputTestDataBuilder.existing().build();
        CustomerSummaryOutput customer2 = CustomerSummaryOutputTestDataBuilder.existingAlt1().build();

        when(customerQueryService.filter(any(CustomerFilter.class)))
                .thenReturn(new PageImpl<>(List.of(customer1, customer2)));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        RestAssuredMockMvc
                .given()
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
                        "content[0].registeredAt", Matchers.is(formatter.format(customer1.getRegisteredAt())),
                        "content[0].archived", Matchers.is(customer1.getArchived()),
                        "content[0].archivedAt", Matchers.nullValue(),
                        "content[0].loyaltyPoints", Matchers.is(customer1.getLoyaltyPoints()),
                        "content[0].promotionNotificationsAllowed", Matchers.is(customer1.getPromotionNotificationsAllowed()),

                        "content[1].id", Matchers.is(customer2.getId().toString()),
                        "content[1].firstName", Matchers.is(customer2.getFirstName()),
                        "content[1].lastName", Matchers.is(customer2.getLastName()),
                        "content[1].email", Matchers.is(customer2.getEmail()),
                        "content[1].document", Matchers.is(customer2.getDocument()),
                        "content[1].phone", Matchers.is(customer2.getPhone()),
                        "content[1].birthDate", Matchers.is(customer2.getBirthDate().toString()),
                        "content[1].registeredAt", Matchers.is(formatter.format(customer2.getRegisteredAt())),
                        "content[1].archived", Matchers.is(customer2.getArchived()),
                        "content[1].archivedAt", Matchers.nullValue(),
                        "content[1].loyaltyPoints", Matchers.is(customer2.getLoyaltyPoints()),
                        "content[1].promotionNotificationsAllowed", Matchers.is(customer2.getPromotionNotificationsAllowed())
                );
    }

    @Test
    void findByIdContract() {
        CustomerOutput customer = CustomerOutputTestDataBuilder.existing().build();

        when(customerQueryService.findById(customer.getId()))
                .thenReturn(customer);

        AddressData address = customer.getAddress();
        RestAssuredMockMvc
                .given()
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
                        "promotionNotificationsAllowed", Matchers.is(customer.getPromotionNotificationsAllowed()),
                        "registeredAt", Matchers.notNullValue(),
                        "archived", Matchers.is(false),
                        "loyaltyPoints", Matchers.is(0),

                        "address.street", Matchers.is(address.getStreet()),
                        "address.number", Matchers.is(address.getNumber()),
                        "address.complement", Matchers.is(address.getComplement()),
                        "address.neighborhood", Matchers.is(address.getNeighborhood()),
                        "address.city", Matchers.is(address.getCity()),
                        "address.state", Matchers.is(address.getState()),
                        "address.zipCode", Matchers.is(address.getZipCode())
                );
    }

    @Test
    void findByIdError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        when(customerQueryService.findById(invalidCustomerId))
                .thenThrow(CustomerNotFoundException.class);

        RestAssuredMockMvc
                .given()
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
                            "timestamp", Matchers.notNullValue()
                    );
    }
}