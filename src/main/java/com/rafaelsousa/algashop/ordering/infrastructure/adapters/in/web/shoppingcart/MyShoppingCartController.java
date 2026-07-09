package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import static com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.*;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.*;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.UnprocessableEntityException;
import com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.CanWriteMyShoppingCart;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/me/shopping-cart")
public class MyShoppingCartController {
    private final ForManagingShoppingCarts forManagingShoppingCarts;
    private final ForQueryingShoppingCarts forQueryingShoppingCarts;
    private final SecurityChecks securityChecks;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteMyShoppingCart
    public ShoppingCartOutput create() {
        try {
            forManagingShoppingCarts.createNew(securityChecks.getAuthenticatedUserId());
        } catch (CustomerNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return getAuthenticatedCustomerShoppingCart();
    }

    @GetMapping
    @CanReadMyShoppingCart
    public ShoppingCartOutput get() {
        return getAuthenticatedCustomerShoppingCart();
    }

    @GetMapping("/items")
    @CanReadMyShoppingCart
    public ShoppingCartItemList getItems() {
        return new ShoppingCartItemList(getAuthenticatedCustomerShoppingCart().getItems());
    }

    @DeleteMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CanWriteMyShoppingCart
    public void removeItems() {
        ShoppingCartOutput shoppingCartOutput = getAuthenticatedCustomerShoppingCart();
        forManagingShoppingCarts.empty(shoppingCartOutput.getId());
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CanWriteMyShoppingCart
    public void addItem(@Valid @RequestBody ShoppingCartItemInput shoppingCartItemInput) {
        ShoppingCartOutput shoppingCartOutput = getAuthenticatedCustomerShoppingCart();
        shoppingCartItemInput.setShoppingCartId(shoppingCartOutput.getId());

        try {
            forManagingShoppingCarts.addItem(shoppingCartItemInput);
        } catch (ProductNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CanWriteMyShoppingCart
    public void removeItemById(@PathVariable UUID itemId) {
        ShoppingCartOutput shoppingCartOutput = getAuthenticatedCustomerShoppingCart();
        forManagingShoppingCarts.removeItem(shoppingCartOutput.getId(), itemId);
    }

    private ShoppingCartOutput getAuthenticatedCustomerShoppingCart() {
        return forQueryingShoppingCarts.findByCustomerId(securityChecks.getAuthenticatedUserId());
    }
}
