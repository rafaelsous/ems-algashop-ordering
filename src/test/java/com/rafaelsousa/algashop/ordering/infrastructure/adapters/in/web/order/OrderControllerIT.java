package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order;

import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.rafaelsousa.algashop.ordering.utils.AlgaShopResourceUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class OrderControllerIT extends AbstractPresentationIT {

    @Autowired
    private CustomerPersistenceRepository customerPersistenceRepository;

    @Autowired
    private OrderPersistenceRepository orderPersistenceRepository;

    @Autowired
    private ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;

    private static final UUID VALID_CUSTOMER_ID = UUID.fromString("9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d");
    private static final UUID VALID_SHOPPING_CART_ID = UUID.fromString("28fcd9fb-4ce7-44d6-9583-14d8b3dc5aff");

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
    void shouldCreateOrderWithProduct() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");

        String createdOrderId = givenAuthenticatedRequest()
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
    void shouldNotCreateOrderWithProductWhenProductNotExists() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-invalid-product.json");

	    givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }

    @Test
    void shouldNotCreateOrderWithProductWhenCustomerWasNotFound() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product-and-invalid-customer.json");

	    givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }

    @Test
    void shouldCreateOrderWithShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-shopping-cart.json");

        ShoppingCartPersistence shoppingCartPersistence = ShoppingCartPersistenceTestDataBuilder.existingShoppingCart()
                .id(VALID_SHOPPING_CART_ID)
                .customer(customerPersistenceRepository.getReferenceById(VALID_CUSTOMER_ID))
                .build();

        shoppingCartPersistenceRepository.saveAndFlush(shoppingCartPersistence);

        String createdOrderId = givenAuthenticatedRequest()
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

	    givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }
}