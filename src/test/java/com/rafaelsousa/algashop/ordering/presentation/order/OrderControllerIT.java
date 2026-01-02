package com.rafaelsousa.algashop.ordering.presentation.order;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.OrderPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceRepository;
import com.rafaelsousa.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
/*@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL
        , ids = "com.rafaelsousa.algashop:product-catalog:0.0.1-SNAPSHOT:8781")*/
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Devido exceção java.net.SocketException: Uma conexão estabelecida foi anulada pelo ‘software’ no computador host
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration,classpath:db/testdata")
class OrderControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerPersistenceRepository customerPersistenceRepository;

    @Autowired
    private OrderPersistenceRepository orderPersistenceRepository;

    @Autowired
    private ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;

    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d");
    private static final UUID VALID_SHOPPING_CART_ID = UUID.fromString("28fcd9fb-4ce7-44d6-9583-14d8b3dc5aff");

    private WireMockServer wireMockRapidex;
    private WireMockServer wireMockProductCatalog;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        wireMockRapidex = new WireMockServer(options()
                        .port(8780)
                        .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex")
                        .extensions(new ResponseTemplateTransformer(true)));

        wireMockProductCatalog = new WireMockServer(options()
                        .port(8781)
                        .usingFilesUnderDirectory("src/test/resources/wiremock/product-catalog")
                        .extensions(new ResponseTemplateTransformer(true)));

        wireMockRapidex.start();
        wireMockProductCatalog.start();
    }

    @AfterEach
    void after() {
        wireMockRapidex.stop();
        wireMockProductCatalog.stop();
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

    @Test
    void shouldNotCreateOrderWithProductWhenProductNotExists() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-invalid-product.json");

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

    @Test
    void shouldCreateOrderWithShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-shopping-cart.json");

        ShoppingCartPersistence shoppingCartPersistence = ShoppingCartPersistenceTestDataBuilder.existingShoppingCart()
                .id(VALID_SHOPPING_CART_ID)
                .customer(customerPersistenceRepository.getReferenceById(VALID_CUSTOMER_ID))
                .build();

        shoppingCartPersistenceRepository.saveAndFlush(shoppingCartPersistence);

        String createdOrderId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body(
                        "id", Matchers.not(Matchers.emptyString()),
                        "customer.firstName", Matchers.not(Matchers.emptyString()),
                        "customer.lastName", Matchers.not(Matchers.emptyString()),
                        "customer.document", Matchers.not(Matchers.emptyString()),
                        "customer.phone", Matchers.not(Matchers.emptyString()))
            .extract()
                .body().as(OrderDetailOutput.class).getId();

        boolean orderExists = orderPersistenceRepository.existsById(new OrderId(createdOrderId).value().toLong());
        assertThat(orderExists).isTrue();
    }

    @Test
    void shouldNotCreateOrderWithInexistentShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-invalid-shopping-cart.json");

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
