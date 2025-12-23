package com.rafaelsousa.algashop.ordering.presentation.shoppingcart;

import com.rafaelsousa.algashop.ordering.application.shoppingcart.management.ShoppingCartInput;
import com.rafaelsousa.algashop.ordering.application.shoppingcart.management.ShoppingCartItemInput;
import com.rafaelsousa.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import com.rafaelsousa.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import com.rafaelsousa.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.rafaelsousa.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
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
    private final ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;
    private final ShoppingCartQueryService shoppingCartQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartOutput create(@Valid @RequestBody ShoppingCartInput shoppingCartInput) {
        UUID shoppingCartId = shoppingCartManagementApplicationService.createNew(shoppingCartInput.getCustomerId());

        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCartOutput findById(@PathVariable("shoppingCartId") UUID shoppingCartId) {
        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    public List<ShoppingCartItemOutput> findShoppingCartItems(@PathVariable("shoppingCartId") UUID shoppingCartId) {
        return shoppingCartQueryService.findById(shoppingCartId).getItems();
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable("shoppingCartId") UUID shoppingCartId) {
        shoppingCartManagementApplicationService.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItems(@PathVariable("shoppingCartId") UUID shoppingCartId) {
        shoppingCartManagementApplicationService.empty(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable("shoppingCartId") UUID shoppingCartId, @Valid @RequestBody ShoppingCartItemInput shoppingCartItemInput) {
        shoppingCartItemInput.setShoppingCartId(shoppingCartId);
        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);
    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemById(@PathVariable("shoppingCartId") UUID shoppingCartId, @PathVariable("itemId") UUID itemId) {
        shoppingCartManagementApplicationService.removeItem(shoppingCartId, itemId);
    }
}