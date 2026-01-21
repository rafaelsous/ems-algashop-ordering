package com.rafaelsousa.algashop.ordering.contract.base;

import com.rafaelsousa.algashop.ordering.core.application.shoppingcart.ShoppingCartManagementApplicationService;
import com.rafaelsousa.algashop.ordering.core.application.shoppingcart.query.ShoppingCartOutputTestDataBuilder;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ForQueryingShoppingCarts;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.rafaelsousa.algashop.ordering.presentation.shoppingcart.ShoppingCartController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ShoppingCartController.class)
class ShoppingCartBase {
    public static final UUID NOT_FOUND_SHOPPING_ID = UUID.fromString("019b3d32-83b4-716d-ac5f-2a5042bddb61");
    public static final UUID VALID_SHOPPING_ID = UUID.fromString("019b3d31-f100-78c7-b442-7ac8b336927c");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ForQueryingShoppingCarts shoppingCartQueryService;

    @MockitoBean
    private ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockCreate();
        mockFindById();
        mockFindByIdNotFound();
    }

    private void mockCreate() {
        when(shoppingCartManagementApplicationService.createNew(any(UUID.class)))
                .thenReturn(VALID_SHOPPING_ID);
    }

    private void mockFindById() {
        when(shoppingCartQueryService.findById(VALID_SHOPPING_ID))
                .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCart().id(VALID_SHOPPING_ID).build());
    }

    private void mockFindByIdNotFound() {
        when(shoppingCartQueryService.findById(NOT_FOUND_SHOPPING_ID))
                .thenThrow(new ShoppingCartNotFoundException(new ShoppingCartId()));
    }
}