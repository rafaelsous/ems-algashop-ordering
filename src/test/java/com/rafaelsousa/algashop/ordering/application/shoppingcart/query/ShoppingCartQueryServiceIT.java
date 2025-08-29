package com.rafaelsousa.algashop.ordering.application.shoppingcart.query;

import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class ShoppingCartQueryServiceIT {
    private final Customers customers;
    private final ShoppingCarts shoppingCarts;
    private final ShoppingCartQueryService shoppingCartQueryService;

    @Autowired
    ShoppingCartQueryServiceIT(Customers customers, ShoppingCarts shoppingCarts, ShoppingCartQueryService shoppingCartQueryService) {
        this.customers = customers;
        this.shoppingCarts = shoppingCarts;
        this.shoppingCartQueryService = shoppingCartQueryService;
    }

    @Test
    void shouldFindShoppingCartById() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        shoppingCarts.add(shoppingCart);

        UUID rawShoppingCartId = shoppingCart.id().value();
        ShoppingCartOutput shoppingCartOutput = shoppingCartQueryService.findById(rawShoppingCartId);

        assertThat(shoppingCartOutput).satisfies(
                output -> {
                    assertThat(output.getId()).isEqualTo(rawShoppingCartId);
                    assertThat(output.getItems()).hasSize(shoppingCart.items().size());
                    assertThat(output.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value());
                    assertThat(output.getTotalItems()).isEqualTo(shoppingCart.totalItems().value());
                }
        );
    }

    @Test
    void shouldThrowExceptionWhenTryingFindByIdWithNonExistentShoppingCart() {
        UUID nonExistentShoppingCartId = new ShoppingCartId().value();

        assertThatThrownBy(() -> shoppingCartQueryService.findById(nonExistentShoppingCartId))
                .isInstanceOf(ShoppingCartNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND.formatted(nonExistentShoppingCartId));
    }

    @Test
    void shouldFindShoppingCartByCustomerId() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        shoppingCarts.add(shoppingCart);

        UUID rawCustomerId = customer.id().value();
        ShoppingCartOutput shoppingCartOutput = shoppingCartQueryService.findByCustomerId(rawCustomerId);

        assertThat(shoppingCartOutput).satisfies(
                output -> {
                    assertThat(output.getId()).isEqualTo(shoppingCart.id().value());
                    assertThat(output.getCustomerId()).isEqualTo(rawCustomerId);
                    assertThat(output.getItems()).hasSize(shoppingCart.items().size());
                    assertThat(output.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value());
                    assertThat(output.getTotalItems()).isEqualTo(shoppingCart.totalItems().value());
                }
        );
    }

    @Test
    void shouldThrowExceptionWhenTryingFindByCustomerIdWithNonExistentCustomer() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        shoppingCarts.add(shoppingCart);

        UUID nonExistentCustomerId = new CustomerId().value();

        assertThatThrownBy(() -> shoppingCartQueryService.findByCustomerId(nonExistentCustomerId))
                .isInstanceOf(ShoppingCartNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND_FOR_CUSTOMER.formatted(nonExistentCustomerId));
    }
}