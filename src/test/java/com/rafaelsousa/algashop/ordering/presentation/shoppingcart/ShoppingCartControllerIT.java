package com.rafaelsousa.algashop.ordering.presentation.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartItemInput;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.shoppingcart.ShoppingCartInput;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceRepository;
import com.rafaelsousa.algashop.ordering.presentation.AbstractPresentationIT;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartControllerIT extends AbstractPresentationIT {

    @Autowired
    private ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;

    private static final UUID VALID_SHOPPING_CART_ID = UUID.fromString("4f31582a-66e6-4601-a9d3-ff608c2d4461");
    private static final UUID VALID_PRODUCT_ID = UUID.fromString("0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa");
    private static final UUID INVALID_SHOPPING_CART_ID = UUID.fromString("019b7580-c053-766f-8659-d511f2d78b44");

    @BeforeEach
    void setUp() {
        super.beforeEach();
    }

    @BeforeAll
    static void beforeAll() {
        initWireMock();
    }

    @AfterAll
    static void afterAll() {
        stopWireMock();
    }

    @Test
    void shouldCreateShoppingCart() {
        ShoppingCartInput shoppingCartInput = ShoppingCartInput.builder()
                .customerId(UUID.fromString("5f6b7d8e-9c0a-1b2d-3c4a-5f6b7d8e9c0a")).build();

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
        ShoppingCartPersistence shoppingCartFromDatabase = shoppingCartPersistenceRepository.findById(VALID_SHOPPING_CART_ID).orElseThrow();
        int beforeQuantity = shoppingCartFromDatabase.getTotalItems();
        int addedQuantity = 2;

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(VALID_SHOPPING_CART_ID)
                .productId(VALID_PRODUCT_ID)
                .quantity(addedQuantity)
                .build();

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(shoppingCartItemInput)
            .when()
                .post("api/v1/shopping-carts/{shoppingCartId}/items", VALID_SHOPPING_CART_ID)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        shoppingCartFromDatabase = shoppingCartPersistenceRepository
                .findById(VALID_SHOPPING_CART_ID).orElseThrow();

        int afterQuantity = shoppingCartFromDatabase.getTotalItems();
        BigDecimal expectedAmount = BigDecimal.valueOf(afterQuantity * 1000L);

        assertThat(afterQuantity).isEqualTo(beforeQuantity + addedQuantity);
        assertThat(shoppingCartFromDatabase.getTotalAmount()).isEqualByComparingTo(expectedAmount);
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