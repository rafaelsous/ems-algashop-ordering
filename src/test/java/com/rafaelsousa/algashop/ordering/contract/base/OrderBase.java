package com.rafaelsousa.algashop.ordering.contract.base;

import com.rafaelsousa.algashop.ordering.application.order.query.OrderDetailOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderQueryService;
import com.rafaelsousa.algashop.ordering.presentation.OrderController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = OrderController.class)
class OrderBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        String id = "0N7ZHVJXN94S6";
        when(orderQueryService.findById(id))
                .thenReturn(OrderDetailOutputTestDataBuilder.placedOrder(id).build());
    }
}