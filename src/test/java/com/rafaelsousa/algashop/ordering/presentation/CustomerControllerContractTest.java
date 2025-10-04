package com.rafaelsousa.algashop.ordering.presentation;

import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerInput;
import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerQueryService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
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
        when(customerManagementApplicationService.create(any(CustomerInput.class)))
                .thenReturn(UUID.randomUUID());
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
}