package com.rafaelsousa.algashop.ordering.presentation.shoppingcart;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.rafaelsousa.algashop.ordering.application.shoppingcart.management.ShoppingCartInput;
import com.rafaelsousa.algashop.ordering.application.shoppingcart.management.ShoppingCartItemInput;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceRepository;
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
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingCartControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerPersistenceRepository customerPersistenceRepository;

    @Autowired
    private ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;

    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");
    private static final UUID VALID_PRODUCT_ID = UUID.fromString("0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa");
    private static final UUID INVALID_SHOPPING_CART_ID = UUID.fromString("019b7580-c053-766f-8659-d511f2d78b44");

    private WireMockServer wireMockRapidex;
    private WireMockServer wireMockProductCatalog;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        initDatabase();

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

    private void initDatabase() {
        customerPersistenceRepository.saveAndFlush(
                CustomerPersistenceTestDataBuilder.aCustomer().id(VALID_CUSTOMER_ID).build()
        );
    }

    @Test
    void shouldCreateShoppingCart() {
        ShoppingCartInput shoppingCartInput = ShoppingCartInput.builder().customerId(VALID_CUSTOMER_ID).build();

        String createdShoppingCartId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(shoppingCartInput)
            .when()
                .post("api/v1/shopping-carts")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body(
                        "id", Matchers.not(Matchers.emptyString())
                )
            .extract()
                .jsonPath().getString("id");

        boolean shoppingCartExists = shoppingCartPersistenceRepository.existsById(UUID.fromString(createdShoppingCartId));
        assertThat(shoppingCartExists).isTrue();
    }

    @Test
    void shouldNotCreateShoppingCartWithInvalidData() {
        ShoppingCartInput shoppingCartInput = ShoppingCartInput.builder().build();

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(shoppingCartInput)
            .when()
                .post("api/v1/shopping-carts")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldAddItemToShoppingCart() {
        ShoppingCartPersistence shoppingCartPersistence = ShoppingCartPersistenceTestDataBuilder.existingShoppingCart()
                .customer(customerPersistenceRepository.getReferenceById(VALID_CUSTOMER_ID))
                .items(new HashSet<>())
                .build();

        shoppingCartPersistenceRepository.saveAndFlush(shoppingCartPersistence);

        int quantity = 2;

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCartPersistence.getId())
                .productId(VALID_PRODUCT_ID)
                .quantity(quantity)
                .build();

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(shoppingCartItemInput)
            .when()
                .post("api/v1/shopping-carts/{shoppingCartId}/items", shoppingCartPersistence.getId())
            .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        ShoppingCartPersistence shoppingCartFromDatabase = shoppingCartPersistenceRepository
                .findByCustomerId(VALID_CUSTOMER_ID).orElseThrow();

        assertThat(shoppingCartFromDatabase.getTotalItems()).isEqualTo(quantity);
        assertThat(shoppingCartFromDatabase.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(2000.00));
    }

    @Test
    void shouldNotAddItemToInexistentShoppingCart() {
        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(INVALID_SHOPPING_CART_ID)
                .productId(VALID_PRODUCT_ID)
                .quantity(2)
                .build();

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(shoppingCartItemInput)
            .when()
                .post("api/v1/shopping-carts/{shoppingCartId}/items", INVALID_SHOPPING_CART_ID)
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}