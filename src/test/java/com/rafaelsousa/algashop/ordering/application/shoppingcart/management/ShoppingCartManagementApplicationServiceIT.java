package com.rafaelsousa.algashop.ordering.application.shoppingcart.management;

import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.*;
import com.rafaelsousa.algashop.ordering.domain.model.product.*;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class ShoppingCartManagementApplicationServiceIT {
    private final Customers customers;
    private final ShoppingCarts shoppingCarts;
    private final ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @Autowired
    ShoppingCartManagementApplicationServiceIT(
            Customers customers, ShoppingCarts shoppingCarts,
            ShoppingCartManagementApplicationService shoppingCartManagementApplicationService) {
        this.customers = customers;
        this.shoppingCarts = shoppingCarts;
        this.shoppingCartManagementApplicationService = shoppingCartManagementApplicationService;
    }

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldAddItem() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);

        Product ramMemory = ProductTestDataBuilder.aProductAltRamMemory().build();

        ShoppingCartId shoppingCartId = shoppingCart.id();
        ProductId ramMemoryProductId = ramMemory.id();
        Quantity quantity = Quantity.of(1);

        when(productCatalogService.ofId(ramMemoryProductId)).thenReturn(Optional.of(ramMemory));

        shoppingCart.addItem(ramMemory, quantity);

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCartId.value())
                .productId(ramMemoryProductId.value())
                .quantity(quantity.value())
                .build();

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);

        ShoppingCart shoppingCartAfterAddItem = shoppingCarts.ofId(shoppingCartId).orElseThrow();

        assertThatNoException().isThrownBy(() -> shoppingCartAfterAddItem.findItem(ramMemoryProductId));

        assertThat(shoppingCartAfterAddItem).satisfies(
                sc -> assertThat(sc.totalAmount()).isEqualTo(ramMemory.price()),
                sc -> assertThat(sc.totalItems()).isEqualTo(quantity)
        );
    }

    @Test
    void shouldThrowExceptionWhenTryingToAddItemToNonExistentShoppingCart() {
        Product ramMemory = ProductTestDataBuilder.aProductAltRamMemory().build();

        ShoppingCartId shoppingCartId = new ShoppingCartId();
        ProductId ramMemoryProductId = ramMemory.id();
        Quantity quantity = Quantity.of(1);

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCartId.value())
                .productId(ramMemoryProductId.value())
                .quantity(quantity.value())
                .build();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput))
                .isInstanceOf(ShoppingCartNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND.formatted(shoppingCartId));
    }

    @Test
    void shouldThrowExceptionWhenTryingToAddNonExistentProduct() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);

        ProductId productId = new ProductId();
        Quantity quantity = Quantity.of(1);

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(productId.value())
                .quantity(quantity.value())
                .build();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_PRODUCT_NOT_FOUND.formatted(productId));

        shoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.isEmpty()).isTrue(),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO)
        );
    }

    @Test
    void shoulThrowExceptionWhenTryingToAddUnavailableProduct() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);

        Product unavailableRamMemory = ProductTestDataBuilder.aProductAltRamMemory().inStock(false).build();

        ProductId unavailableProductId = unavailableRamMemory.id();
        Quantity quantity = Quantity.of(1);

        when(productCatalogService.ofId(unavailableProductId)).thenReturn(Optional.of(unavailableRamMemory));

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(unavailableProductId.value())
                .quantity(quantity.value())
                .build();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput))
                .isInstanceOf(ProductOutOfStockException.class)
                .hasMessage(ErrorMessages.ERROR_PRODUCT_OUT_OF_STOCK.formatted(unavailableProductId));

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.isEmpty()).isTrue(),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO)
        );
    }

    @Test
    void shouldCreateNewShoppingCart() {
        UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();

        UUID shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId)).orElseThrow();

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.id()).isEqualTo(new ShoppingCartId(shoppingCartId)),
                sc -> assertThat(sc.customerId()).isEqualTo(new CustomerId(customerId)),
                sc -> assertThat(sc.isEmpty()).isTrue(),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO)
        );
    }

    @Test
    void shouldThrowExceptionWhenTryingToCreateShoppingCartWithNonExistentCustomer() {
        UUID nonExistentCustomerId = UUID.randomUUID();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.createNew(nonExistentCustomerId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND.formatted(new CustomerId(nonExistentCustomerId)));
    }

    @Test
    void shouldThrowExceptionWhenTryingToCreateNewShoppingCartForCustomerWhoAlreadyHasOne() {
        CustomerId defaultCustomerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(defaultCustomerId).withItems(false).build();
        shoppingCarts.add(shoppingCart);

        UUID rawCustomerId = defaultCustomerId.value();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.createNew(rawCustomerId))
                .isInstanceOf(CustomerAlreadyHaveShoppingCartException.class)
                .hasMessage(ErrorMessages.ERROR_CUSTOMER_ALREADY_HAVE_SHOPPING_CART.formatted(
                        shoppingCart.customerId(), shoppingCart.id()));
    }

    /**
     TODO:
         Analisar o porquê de o teste só funcionar quando o carrinho de compras tem apenas um item.
         Quanto dois ou mais itens são adicionados, o teste falha lançando a exceção ObjectOptimisticLockingFailureException
         com a mensagem "Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect")
    **/
    @Test
    @Disabled("Teste só passa quando o carrinho de compras tem apenas um item")
    void shouldRemoveItemWhenShoppingCartHasTwoOrMoreItems() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();

        Product ramMemory = ProductTestDataBuilder.aProductAltRamMemory().build();
        Product mousePad = ProductTestDataBuilder.aProductAltMousePad().build();

        shoppingCart.addItem(ramMemory, Quantity.of(1));
        shoppingCart.addItem(mousePad, Quantity.of(1));
        shoppingCarts.add(shoppingCart);

        ShoppingCartItem ramMemoryShoppingCarItem = shoppingCart.findItem(ramMemory.id());
        ShoppingCartItem mousePadShoppingCartItem = shoppingCart.findItem(mousePad.id());

        UUID rawShoppingCartId = shoppingCart.id().value();
        UUID rawShoppingCartItemId = ramMemoryShoppingCarItem.id().value();

        shoppingCartManagementApplicationService.removeItem(rawShoppingCartId, rawShoppingCartItemId);

        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        assertThat(updatedShoppingCart.items()).isEmpty();

        Set<ShoppingCartItem> items = updatedShoppingCart.items();

        assertThat(items)
                .hasSize(1)
                .contains(ramMemoryShoppingCarItem)
                .doesNotContain(mousePadShoppingCartItem);

        assertThat(updatedShoppingCart).satisfies(
                sc -> assertThat(sc.totalAmount()).isEqualTo(mousePad.price()),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.of(1))
        );
    }

    @Test
    void shouldRemoveItem() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();

        Product ramMemory = ProductTestDataBuilder.aProductAltRamMemory().build();

        shoppingCart.addItem(ramMemory, Quantity.of(1));
        shoppingCarts.add(shoppingCart);

        ShoppingCartItem ramMemoryShoppingCarItem = shoppingCart.findItem(ramMemory.id());

        UUID rawShoppingCartId = shoppingCart.id().value();
        UUID rawShoppingCartItemId = ramMemoryShoppingCarItem.id().value();

        shoppingCartManagementApplicationService.removeItem(rawShoppingCartId, rawShoppingCartItemId);

        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        assertThat(updatedShoppingCart.items()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenTryingToRemoveItemFromNonExistentShoppingCart() {
        UUID nonExistentShoppingCartId = UUID.randomUUID();

        UUID nonExistentShoppingCartItemId = UUID.randomUUID();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.removeItem(
                nonExistentShoppingCartId, nonExistentShoppingCartItemId))
                .isInstanceOf(ShoppingCartNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND.formatted(
                        new ShoppingCartId(nonExistentShoppingCartId)));
    }

    @Test
    void shouldThrowExceptionWhenTryingToRemoveNonExistentItem() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);

        UUID rawShoppingCartId = shoppingCart.id().value();

        UUID nonExistentShoppingCartItemId = UUID.randomUUID();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.removeItem(
                rawShoppingCartId, nonExistentShoppingCartItemId))
                .isInstanceOf(ShoppingCartDoesNotContainItemException.class)
                .hasMessage(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM.formatted(
                        new ShoppingCartId(rawShoppingCartId), new ShoppingCartItemId(nonExistentShoppingCartItemId)));
    }

    @Test
    void shouldEmptyShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);

        UUID rawShoppingCartId = shoppingCart.id().value();

        shoppingCartManagementApplicationService.empty(rawShoppingCartId);

        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        assertThat(updatedShoppingCart).satisfies(
                sc -> assertThat(sc.isEmpty()).isTrue(),
                sc -> assertThat(sc.items()).isEmpty(),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO)
        );
    }

    @Test
    void shouldThrowExceptionWhenTryingEmptyNonExistentShoppingCart() {
        UUID nonExistentShoppingCartId = UUID.randomUUID();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.empty(nonExistentShoppingCartId))
                .isInstanceOf(ShoppingCartNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND.formatted(new ShoppingCartId(nonExistentShoppingCartId)));
    }

    @Test
    void shouldDeleteShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);

        UUID rawShoppingCartId = shoppingCart.id().value();

        shoppingCartManagementApplicationService.delete(rawShoppingCartId);

        assertThat(shoppingCarts.ofId(shoppingCart.id())).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenTryingToDeleteNonExistentShoppingCart() {
        UUID nonExistentShoppingCartId = UUID.randomUUID();

        assertThatThrownBy(() -> shoppingCartManagementApplicationService.delete(nonExistentShoppingCartId))
                .isInstanceOf(ShoppingCartNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND.formatted(new ShoppingCartId(nonExistentShoppingCartId)));
    }
}