package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.rafaelsousa.algashop.ordering.core.domain.model.IdGenerator;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceRepository;
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

class MyOrdersControllerIT extends AbstractPresentationIT {

    @Autowired private OrderPersistenceRepository orderPersistenceRepository;

    private static final UUID VALID_CUSTOMER_ID =
            UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

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
    void shouldFindMyOrderById() {
        String orderId = "00001J8JEEVR1";

        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/me/orders/{orderId}", orderId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "id", Matchers.is(orderId),
                        "customer.id", Matchers.is(VALID_CUSTOMER_ID.toString()));
    }

    @Test
    void shouldNotFoundMyOrderWhenOrderNotExists() {
        String inexistentOrderId = IdGenerator.generateTSID().toString();

        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/me/orders/{orderId}", inexistentOrderId)
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldListMyOrders() {
        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/me/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "content", Matchers.not(Matchers.empty()),
                        "content[0].customer.id", Matchers.is(VALID_CUSTOMER_ID.toString()));
    }

    @Test
    void shouldCreateOrderWithProduct() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");

        String createdOrderId =
                givenAuthenticatedRequest()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType("application/vnd.order-with-product.v1+json")
                        .body(json)
                        .when()
                        .post("/api/v1/customers/me/orders")
                        .then()
                        .assertThat()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .statusCode(HttpStatus.CREATED.value())
                        .body(
                                "id", Matchers.not(Matchers.emptyString()),
                                "customer.id", Matchers.is(VALID_CUSTOMER_ID.toString()))
                        .extract()
                        .jsonPath()
                        .getString("id");

        boolean orderExists =
                orderPersistenceRepository.existsById(new OrderId(createdOrderId).value().toLong());
        assertThat(orderExists).isTrue();
    }

    @Test
    void shouldNotCreateOrderWithProductWhenProductNotExists() {
        String json =
                AlgaShopResourceUtils.readContent("json/create-order-with-invalid-product.json");

        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
                .when()
                .post("/api/v1/customers/me/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }

    @Test
    void shouldNotCreateOrderWithProductWhenCustomerWasNotFound() {
        String json =
                AlgaShopResourceUtils.readContent(
                        "json/create-order-with-product-and-invalid-customer.json");

        givenAuthenticatedRequest()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
                .when()
                .post("/api/v1/customers/me/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }

    @Test
    void shouldCreateOrderWithShoppingCart() {
        String json =
                AlgaShopResourceUtils.readContent("json/create-order-with-shopping-cart.json");

        String createdOrderId =
                givenAuthenticatedRequest()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType("application/vnd.order-with-shopping-cart.v1+json")
                        .body(json)
                        .when()
                        .post("/api/v1/customers/me/orders")
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
                        .body()
                        .as(OrderDetailOutput.class)
                        .getId();

        boolean orderExists =
                orderPersistenceRepository.existsById(new OrderId(createdOrderId).value().toLong());
        assertThat(orderExists).isTrue();
    }

    @Test
    void shouldNotCreateOrderWithShoppingCartWhenCustomerWasNotFound() {
        String json =
                AlgaShopResourceUtils.readContent(
                        "json/create-order-with-invalid-shopping-cart.json");

        givenAuthenticatedAltCustomer()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
                .when()
                .post("/api/v1/customers/me/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }
}
