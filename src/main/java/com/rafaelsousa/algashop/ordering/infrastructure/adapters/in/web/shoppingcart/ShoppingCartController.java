package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.*;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.ordering.presentation.UnprocessableEntityException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-carts")
public class ShoppingCartController {
    private final ForManagingShoppingCarts forManagingShoppingCarts;
    private final ForQueryingShoppingCarts forQueryingShoppingCarts;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartOutput create(@Valid @RequestBody ShoppingCartInput shoppingCartInput) {
        UUID shoppingCartId;

        try {
            shoppingCartId = forManagingShoppingCarts.createNew(shoppingCartInput.getCustomerId());
        } catch (CustomerNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return forQueryingShoppingCarts.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCartOutput findById(@PathVariable("shoppingCartId") UUID shoppingCartId) {
        return forQueryingShoppingCarts.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    public List<ShoppingCartItemOutput> findShoppingCartItems(@PathVariable("shoppingCartId") UUID shoppingCartId) {
        return forQueryingShoppingCarts.findById(shoppingCartId).getItems();
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable("shoppingCartId") UUID shoppingCartId) {
        forManagingShoppingCarts.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItems(@PathVariable("shoppingCartId") UUID shoppingCartId) {
        forManagingShoppingCarts.empty(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable("shoppingCartId") UUID shoppingCartId, @Valid @RequestBody ShoppingCartItemInput shoppingCartItemInput) {
        shoppingCartItemInput.setShoppingCartId(shoppingCartId);

        try {
            forManagingShoppingCarts.addItem(shoppingCartItemInput);
        } catch (ProductNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemById(@PathVariable("shoppingCartId") UUID shoppingCartId, @PathVariable("itemId") UUID itemId) {
        forManagingShoppingCarts.removeItem(shoppingCartId, itemId);
    }
}