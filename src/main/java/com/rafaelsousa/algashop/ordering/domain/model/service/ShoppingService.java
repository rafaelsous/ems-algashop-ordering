package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.rafaelsousa.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.repository.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.rafaelsousa.algashop.ordering.domain.model.utils.DomainService;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
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