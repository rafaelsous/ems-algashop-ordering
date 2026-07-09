package com.rafaelsousa.algashop.ordering.contract.base;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.core.application.shoppingcart.ShoppingCartManagementApplicationService;
import com.rafaelsousa.algashop.ordering.core.application.shoppingcart.ShoppingCartOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ForQueryingShoppingCarts;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.shoppingcart.MyShoppingCartController;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebMvcTest(controllers = MyShoppingCartController.class)
class ShoppingCartBase {
    public static final UUID VALID_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");
    public static final UUID VALID_SHOPPING_ID = UUID.fromString("019b3d31-f100-78c7-b442-7ac8b336927c");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ForQueryingShoppingCarts shoppingCartQueryService;

    @MockitoBean
    private ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @MockitoBean
    private SecurityChecks securityChecks;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockAuthenticatedUserId();
        mockCreate();
        mockFindById();
    }

    private void mockAuthenticatedUserId() {
        when(securityChecks.getAuthenticatedUserId()).thenReturn(VALID_CUSTOMER_ID);
    }

    private void mockCreate() {
        when(shoppingCartManagementApplicationService.createNew(any(UUID.class)))
                .thenReturn(VALID_SHOPPING_ID);
    }

    private void mockFindById() {
        when(shoppingCartQueryService.findByCustomerId(VALID_CUSTOMER_ID))
                .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCart()
                    .id(VALID_SHOPPING_ID)
                    .customerId(VALID_CUSTOMER_ID)
                    .build());
    }
}