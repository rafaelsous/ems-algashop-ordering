package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.DomainService;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@DomainService
@RequiredArgsConstructor
public class ShoppingService {
    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    public ShoppingCart startShopping(CustomerId customerId) {
        checkIfCustomerExists(customerId);
        checkIfCustomerAlreadyHaveShoppingCart(customerId);

        return ShoppingCart.startShopping(customerId);
    }

    private void checkIfCustomerExists(CustomerId customerId) {
        if (!customers.exists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }

    private void checkIfCustomerAlreadyHaveShoppingCart(CustomerId customerId) {
        Optional<ShoppingCart> shoppingCartOptional = shoppingCarts.ofCustomer(customerId);

        if (shoppingCartOptional.isPresent()) {
            throw new CustomerAlreadyHaveShoppingCartException(customerId, shoppingCartOptional.get().id());
        }
    }
}