package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashSet;

class ShoppingCartTest {

    @Test
    void givenCustomerId_whenStartShoppingCart_shouldGenerateShoppingCartWithDefaultValues() {
        CustomerId customerId = new CustomerId();

        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.id()).isNotNull(),
                sc -> Assertions.assertThat(sc.customerId()).isEqualTo(customerId),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> Assertions.assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO),
                sc -> Assertions.assertThat(sc.createdAt()).isNotNull(),
                sc -> Assertions.assertThat(sc.isEmpty()).isTrue()
        );
    }

    @Test
    void givenEmptyShoppingCart_whenTryToAddOutOfStrockItem_shouldThrowException() {
        CustomerId customerId = new CustomerId();

        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
        Product unavailableProduct = ProductTestDataBuilder.aProductUnavailable().build();
        Quantity quantity = Quantity.of(1);

        Assertions.assertThatThrownBy(() -> shoppingCart.addItem(unavailableProduct, quantity))
                .isInstanceOf(ProductOutOfStockException.class);
    }

    @Test
    void givenEmptyShoppingCart_whenAddTwoIdenticalItems_shouldUpdateShoppingCartValues() {
        CustomerId customerId = new CustomerId();

        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
        Product mousePad = ProductTestDataBuilder.aProductAltMousePad().build();
        Quantity quantity = Quantity.of(1);

        shoppingCart.addItem(mousePad, quantity);
        shoppingCart.addItem(mousePad, quantity);

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.items()).hasSize(1),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(Money.of("200.00")),
                sc -> Assertions.assertThat(sc.totalItems()).isEqualTo(Quantity.of(2))
        );
    }

    @Test
    void givenEmptyShoppingCart_whenAddTwoDifferentItems_shouldContainBothItems() {
        CustomerId customerId = new CustomerId();

        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);
        Product mousePad = ProductTestDataBuilder.aProductAltMousePad().build();
        Product ramMemory = ProductTestDataBuilder.aProductAltRamMemory().build();
        Quantity quantity = Quantity.of(1);

        shoppingCart.addItem(mousePad, quantity);
        shoppingCart.addItem(ramMemory, quantity);

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.items()).hasSize(2),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(Money.of("250.00")),
                sc -> Assertions.assertThat(sc.totalItems()).isEqualTo(Quantity.of(2))
        );
    }

    @Test
    void givenShoppingCartWithItems_whenTryToRemoveInexistentItem_shouldThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        ShoppingCartItemId inexistentShoppingCartItemId = new ShoppingCartItemId();

        Assertions.assertThatThrownBy(() -> shoppingCart.removeItem(inexistentShoppingCartItemId))
                .isInstanceOf(ShoppingCartDoesNotContainItemException.class);
    }

    @Test
    void givenShoppingCartWithItems_whenRemoveExistingItem_shouldRecalculateTotals() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        ShoppingCartItem shoppingCartItemToRemove = shoppingCart.items().iterator().next();

        shoppingCart.removeItem(shoppingCartItemToRemove.id());

        Quantity expectedQuantity = shoppingCart.items().stream()
                .map(ShoppingCartItem::quantity)
                .reduce(Quantity.ZERO, Quantity::add);
        Money expectedTotalAmount = shoppingCart.items().stream()
                .map(ShoppingCartItem::totalAmount)
                .reduce(Money.ZERO, Money::add);

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.totalItems()).isEqualTo(expectedQuantity),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(expectedTotalAmount)
        );
    }

    @Test
    void givenShoppingCartWithItems_whenEmpty_shouldRemoveAllAndRecalculateTotals() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        shoppingCart.empty();

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.isEmpty()).isTrue(),
                sc -> Assertions.assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> Assertions.assertThat(sc.items()).isEmpty()
        );
    }

    @Test
    void givenShoppingCartWithItems_whenChangeItemPrice_shouldRecalculateTotals() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withItems(false)
                .build();

        Product mousePad = ProductTestDataBuilder.aProductAltMousePad().build();

        shoppingCart.addItem(mousePad, Quantity.of(5));


        Money mousePadUpdatedPrice = Money.of("20.00");
        mousePad = ProductTestDataBuilder.aProductAltMousePad()
                .price(mousePadUpdatedPrice)
                .build();

        shoppingCart.refreshItem(mousePad);

        ShoppingCartItem shoppingCartItem = shoppingCart.findItem(mousePad.id());

        Money expectedTotalAmount = Money.of("100.00");

        Assertions.assertThat(shoppingCartItem.price()).isEqualTo(mousePadUpdatedPrice);
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(expectedTotalAmount);
    }

    @Test
    void givenShoppingCartWithItems_whenVerifyUnvailableItem_shouldReturnTrue() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Product unavaliableProduct = ProductTestDataBuilder.aProductAltMousePad()
                .inStock(false)
                .build();

        shoppingCart.refreshItem(unavaliableProduct);

        Assertions.assertThat(shoppingCart.containsUnavailableItems()).isTrue();
    }

    @Test
    void givenShoppingCartWithItems_whenChangeItemQuantity_shouldUpdateItemTotal() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        ShoppingCartItem shoppingCartItemToChangeQuantity = shoppingCart.items().iterator().next();

        shoppingCart.changeItemQuantity(shoppingCartItemToChangeQuantity.id(), Quantity.of(5));

        Quantity expectedNewQuantity = shoppingCart.items().stream()
                .map(ShoppingCartItem::quantity)
                .reduce(Quantity.ZERO, Quantity::add);

        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(expectedNewQuantity);
    }

    @Test
    void givenShoppingCartWithItems_whenChangeItemQuantityToZero_shouldThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        ShoppingCartItem shoppingCartItemToChangeQuantity = shoppingCart.items().iterator().next();
        ShoppingCartItemId shoppingCartItemId = shoppingCartItemToChangeQuantity.id();
        Quantity invalidQuantity = Quantity.of(0);

        Assertions.assertThatThrownBy(
                        () -> shoppingCart.changeItemQuantity(shoppingCartItemId, invalidQuantity))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenTwoShoppingCartsWithDifferentIds_whenCallCompareTo_shouldReturnFalse() {
        ShoppingCart shoppingCart1 = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCart shoppingCart2 = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Assertions.assertThat(shoppingCart1).isNotEqualTo(shoppingCart2);
    }

    @Test
    void givenTwoShoppingCartsWithSameIds_whenCallCompareTo_shouldReturnTrue() {
        ShoppingCartId shoppingCartId = new ShoppingCartId();

        ShoppingCart shoppingCart1 = ShoppingCart.existing()
                .id(shoppingCartId)
                .customerId(new CustomerId())
                .totalItems(Quantity.of(1))
                .totalAmount(Money.of("500.00"))
                .createdAt(OffsetDateTime.now())
                .items(new HashSet<>())
                .build();

        ShoppingCart shoppingCart2 = ShoppingCart.existing()
                .id(shoppingCartId)
                .customerId(new CustomerId())
                .totalItems(Quantity.of(2))
                .totalAmount(Money.of("600.00"))
                .createdAt(OffsetDateTime.now().plusMinutes(20))
                .items(new HashSet<>())
                .build();

        Assertions.assertThat(shoppingCart1).isEqualTo(shoppingCart2);
        Assertions.assertThat(shoppingCart1.hashCode()).hasSameHashCodeAs(shoppingCart2.hashCode());
    }
}



























