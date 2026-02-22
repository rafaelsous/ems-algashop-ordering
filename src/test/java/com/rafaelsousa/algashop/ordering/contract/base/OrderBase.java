package com.rafaelsousa.algashop.ordering.contract.base;

import com.rafaelsousa.algashop.ordering.core.application.checkout.BuyNowApplicationService;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import com.rafaelsousa.algashop.ordering.core.application.checkout.CheckoutApplicationService;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.CheckoutInput;
import com.rafaelsousa.algashop.ordering.core.application.order.OrderDetailOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.OrderFilter;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import com.rafaelsousa.algashop.ordering.core.application.order.OrderSummaryOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order.OrderController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = OrderController.class)
class OrderBase {
    private static final String NOT_FOUND_ORDER_ID = "0N8N9TJWPSBWK";
    public static final String VALID_ORDER_ID = "0N7ZHVJXN94S6";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ForQueryingOrders orderQueryService;

    @MockitoBean
    private BuyNowApplicationService buyNowApplicationService;

    @MockitoBean
    private CheckoutApplicationService checkoutApplicationService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockValidOrderById();
        mockInvalidOrderById();
        mockValidBuyNow();
        mockValidCheckout();
        mockFilterOrders();
    }

    private void mockValidBuyNow() {
        when(buyNowApplicationService.buyNow(any(BuyNowInput.class)))
                .thenReturn(VALID_ORDER_ID);
    }

    private void mockValidCheckout() {
        when(checkoutApplicationService.checkout(any(CheckoutInput.class)))
                .thenReturn(VALID_ORDER_ID);
    }

    private void mockValidOrderById() {
        when(orderQueryService.findById(VALID_ORDER_ID))
                .thenReturn(OrderDetailOutputTestDataBuilder.placedOrder(VALID_ORDER_ID).build());
    }

    private void mockInvalidOrderById() {
        when(orderQueryService.findById(NOT_FOUND_ORDER_ID))
                .thenThrow(new OrderNotFoundException(new OrderId(NOT_FOUND_ORDER_ID)));
    }

    private void mockFilterOrders() {
        when(orderQueryService.filter(any(OrderFilter.class)))
                .thenReturn(new PageImpl<>(
                        List.of(OrderSummaryOutputTestDataBuilder.placedOrder().id(VALID_ORDER_ID).build())
                ));
    }
}