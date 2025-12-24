package com.rafaelsousa.algashop.ordering.presentation.order;

import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.OrderPersistenceRepository;
import com.rafaelsousa.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static io.restassured.config.JsonConfig.jsonConfig;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIT {

    @LocalServerPort
    private int port;

    private static boolean databaseInitialized;

    @Autowired
    private CustomerPersistenceRepository customerPersistenceRepository;

    @Autowired
    private OrderPersistenceRepository orderPersistenceRepository;

    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @BeforeEach
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        initDatabase();
    }

    private void initDatabase() {
        if (databaseInitialized) return;

        customerPersistenceRepository.saveAndFlush(
                CustomerPersistenceTestDataBuilder.aCustomer().id(VALID_CUSTOMER_ID).build()
        );

        databaseInitialized = true;
    }

    @Test
    void shouldCreateOrderWithProduct() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");

        String createdOrderId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body(
                        "id", Matchers.not(Matchers.emptyString()),
                        "customer.id", Matchers.is(VALID_CUSTOMER_ID.toString()))
            .extract()
                .jsonPath().getString("id");

        boolean orderExists = orderPersistenceRepository.existsById(new OrderId(createdOrderId).value().toLong());
        assertThat(orderExists).isTrue();
    }

    @Test
    void shouldNotCreateOrderWithProductWhenCustomerWasNotFound() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product-and-invalid-customer.json");

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
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
