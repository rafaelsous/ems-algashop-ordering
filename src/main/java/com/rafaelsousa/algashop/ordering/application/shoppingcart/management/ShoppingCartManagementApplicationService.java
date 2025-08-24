package com.rafaelsousa.algashop.ordering.application.shoppingcart.management;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartManagementApplicationService {
    private final ShoppingCarts shoppingCarts;
    private final ProductCatalogService productCatalogService;
    private final ShoppingService shoppingService;

    @Transactional
    public void addItem(ShoppingCartItemInput shoppingCartItemInput) {
        Objects.requireNonNull(shoppingCartItemInput);

        ShoppingCartId shoppingCartId = new ShoppingCartId(shoppingCartItemInput.getShoppingCartId());
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId).orElseThrow(
                () -> new ShoppingCartNotFoundException(shoppingCartId));

        ProductId productId = new ProductId(shoppingCartItemInput.getProductId());
        Product product = productCatalogService.ofId(productId).orElseThrow(
                () -> new ProductNotFoundException(productId));

        Quantity quantity = Quantity.of(shoppingCartItemInput.getQuantity());
        shoppingCart.addItem(product, quantity);

        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public UUID createNew(UUID rawCustomerId) {
        Objects.requireNonNull(rawCustomerId);

        CustomerId customerId = new CustomerId(rawCustomerId);
        ShoppingCart shoppingCart = shoppingService.startShopping(customerId);

        shoppingCarts.add(shoppingCart);

        return shoppingCart.id().value();
    }

    @Transactional
    public void removeItem(UUID rawShoppingCartId, UUID rawShoppingCartItemId) {
        Objects.requireNonNull(rawShoppingCartId);
        Objects.requireNonNull(rawShoppingCartItemId);

        ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        shoppingCart.removeItem(new ShoppingCartItemId(rawShoppingCartItemId));

        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public void empty(UUID rawShoppingCartId) {
        Objects.requireNonNull(rawShoppingCartId);

        ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        shoppingCart.empty();

        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public void delete(UUID rawShoppingCartId) {
        Objects.requireNonNull(rawShoppingCartId);

        ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        shoppingCarts.remove(shoppingCart);
    }
}