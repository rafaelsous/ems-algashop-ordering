package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartItemInput;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceRepository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.UUID;

@Sql(
        scripts = "classpath:db/testdata/afterMigrate.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MyShoppingCartControllerIT extends AbstractPresentationIT {

    @Autowired private ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;

    private static final UUID VALID_SHOPPING_CART_ID =
            UUID.fromString("4f31582a-66e6-4601-a9d3-ff608c2d4461");
    private static final UUID VALID_SHOPPING_CART_ITEM_ID =
            UUID.fromString("8c9a7d6e-5f4c-3b2a-1c0b-9d8e7f6a5b4c");
    private static final UUID VALID_PRODUCT_ID =
            UUID.fromString("0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa");
    private static final UUID INVALID_PRODUCT_ID = UUID.randomUUID();
    private static final UUID INVALID_SHOPPING_CART_ID =
            UUID.fromString("019b7580-c053-766f-8659-d511f2d78b44");

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
    void shouldReturnForbiddenWhenGettingMyShoppingCartWithoutScope() {
        givenAuthenticatedWithNoScopeTokenRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("api/v1/customers/me/shopping-cart")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldCreateShoppingCartForMe() {
        shoppingCartPersistenceRepository.deleteAll();

        String createdShoppingCartId =
                givenAuthenticatedRequest()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .post("api/v1/customers/me/shopping-cart")
                        .then()
                        .assertThat()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .statusCode(HttpStatus.CREATED.value())
                        .body("id", Matchers.not(Matchers.emptyString()))
                        .extract()
                        .jsonPath()
                        .getString("id");

        boolean shoppingCartExists =
                shoppingCartPersistenceRepository.existsById(
                        UUID.fromString(createdShoppingCartId));
        assertThat(shoppingCartExists).isTrue();
    }

    @Test
    void shouldNotCreateShoppingCartForNonExistentCustomer() {
        givenAuthenticatedAltCustomerRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("api/v1/customers/me/shopping-cart")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }

    @Test
    void shouldGetMyShoppingCart() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("api/v1/customers/me/shopping-cart")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.is(VALID_SHOPPING_CART_ID.toString()));
    }

    @Test
    void shouldListMyShoppingCartItems() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("api/v1/customers/me/shopping-cart/items")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "items",
                        Matchers.hasSize(1),
                        "items[0].id",
                        Matchers.is(VALID_SHOPPING_CART_ITEM_ID.toString()));
    }

    @Test
    void shouldEmptyMyShoppingCart() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("api/v1/customers/me/shopping-cart/items")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        ShoppingCartPersistence shoppingCartFromDatabase =
                shoppingCartPersistenceRepository.findById(VALID_SHOPPING_CART_ID).orElseThrow();

        assertThat(shoppingCartFromDatabase)
                .satisfies(
                        sc -> {
                            assertThat(sc.getItems()).isEmpty();
                            assertThat(sc.getTotalItems()).isZero();
                            assertThat(sc.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
                        });
    }

    @Test
    void shouldAddItemToMyShoppingCart() {
        ShoppingCartPersistence shoppingCartFromDatabase =
                shoppingCartPersistenceRepository.findById(VALID_SHOPPING_CART_ID).orElseThrow();
        int beforeQuantity = shoppingCartFromDatabase.getTotalItems();
        int addedQuantity = 2;

        ShoppingCartItemInput shoppingCartItemInput =
                ShoppingCartItemInput.builder()
                        .shoppingCartId(VALID_SHOPPING_CART_ID)
                        .productId(VALID_PRODUCT_ID)
                        .quantity(addedQuantity)
                        .build();

        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(shoppingCartItemInput)
                .when()
                .post("api/v1/customers/me/shopping-cart/items")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        shoppingCartFromDatabase =
                shoppingCartPersistenceRepository.findById(VALID_SHOPPING_CART_ID).orElseThrow();

        int afterQuantity = shoppingCartFromDatabase.getTotalItems();
        BigDecimal expectedAmount = BigDecimal.valueOf(afterQuantity * 1000L);

        assertThat(afterQuantity).isEqualTo(beforeQuantity + addedQuantity);
        assertThat(shoppingCartFromDatabase.getTotalAmount()).isEqualByComparingTo(expectedAmount);
    }

    @Test
    void shouldNotAddItemToInexistentShoppingCart() {
        ShoppingCartItemInput shoppingCartItemInput =
                ShoppingCartItemInput.builder()
                        .shoppingCartId(INVALID_SHOPPING_CART_ID)
                        .productId(VALID_PRODUCT_ID)
                        .quantity(2)
                        .build();

        givenAuthenticatedAltCustomerRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(shoppingCartItemInput)
                .when()
                .post("api/v1/customers/me/shopping-cart/items")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldNotAddInvalidItemToMyShoppingCart() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                        ShoppingCartItemInput.builder()
                                .shoppingCartId(VALID_SHOPPING_CART_ID)
                                .productId(INVALID_PRODUCT_ID)
                                .quantity(2)
                                .build())
                .when()
                .post("api/v1/customers/me/shopping-cart/items")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }

    @Test
    void shouldRemoveItemFromMyShoppingCart() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(
                        "api/v1/customers/me/shopping-cart/items/{itemId}",
                        VALID_SHOPPING_CART_ITEM_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        ShoppingCartPersistence shoppingCartPersistence =
                shoppingCartPersistenceRepository.findById(VALID_SHOPPING_CART_ID).orElseThrow();

        assertThat(shoppingCartPersistence.getItems())
                .noneMatch(item -> VALID_SHOPPING_CART_ITEM_ID.equals(item.getId()));
    }
}
