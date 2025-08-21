package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

        verify(customers, times(1)).exists(inexistingCustomerId);
        verify(shoppingCarts, never()).ofCustomer(inexistingCustomerId);
    }

    @Test
    void givenCustomerThatAlreadyShoppingCart_whenCallStartShopping_shouldThrowException() {
        CustomerId customerId = new CustomerId();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.of(shoppingCart));

        assertThatThrownBy(() -> shoppingService.startShopping(customerId))
                .isInstanceOf(CustomerAlreadyHaveShoppingCartException.class);

        verify(customers, times(1)).exists(customerId);
        verify(shoppingCarts, times(1)).ofCustomer(customerId);
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

        verify(customers, times(1)).exists(customerId);
        verify(shoppingCarts, times(1)).ofCustomer(customerId);
    }
}