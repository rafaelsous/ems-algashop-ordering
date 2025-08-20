package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.rafaelsousa.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.repository.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

    @Mock
    private Customers customers;

    @Mock
    private ShoppingCarts shoppingCarts;

    @InjectMocks
    private ShoppingService shoppingService;

    @Test
    void givenInexistingCustomer_whenCallStartShopping_shouldThrowException() {
        CustomerId inexistingCustomerId = new CustomerId();

        when(customers.exists(inexistingCustomerId)).thenReturn(false);

        assertThatThrownBy(() -> shoppingService.startShopping(inexistingCustomerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void givenCustomerThatAlreadyShoppingCart_whenCallStartShopping_shouldThrowException() {
        CustomerId customerId = new CustomerId();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.of(shoppingCart));

        assertThatThrownBy(() -> shoppingService.startShopping(customerId))
                .isInstanceOf(CustomerAlreadyHaveShoppingCartException.class);
    }

    @Test
    void givenExistingCustomerWithoutShoppingCart_whenCallStartShopping_shouldReturnShoppingCart() {
        CustomerId customerId = new CustomerId();

        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.empty());

        ShoppingCart shoppingCart = shoppingService.startShopping(customerId);

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.id()).isNotNull(),
                sc -> assertThat(sc.customerId()).isEqualTo(customerId),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> assertThat(sc.createdAt()).isNotNull(),
                sc -> assertThat(sc.items()).isEmpty(),
                sc -> assertThat(sc.version()).isNull()
        );
    }
}