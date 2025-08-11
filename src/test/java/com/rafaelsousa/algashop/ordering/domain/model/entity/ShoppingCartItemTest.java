package com.rafaelsousa.algashop.ordering.domain.model.entity;

import com.rafaelsousa.algashop.ordering.domain.model.exception.ShoppingCartItemIncompatibleProductException;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartItemTest {

    @Test
    void givenValidData_whenCreateBrandNew_shouldInstantiateNewShoppingCartItem() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Assertions.assertWith(shoppingCartItem,
                sci -> Assertions.assertThat(sci.id()).isNotNull(),
                sci -> Assertions.assertThat(sci.shoppingCartId()).isNotNull(),
                sci -> Assertions.assertThat(sci.productId()).isNotNull(),
                sci -> Assertions.assertThat(sci.productName()).isEqualTo(ProductName.of("Notebook")),
                sci -> Assertions.assertThat(sci.price()).isEqualTo(Money.of("1000.00")),
                sci -> Assertions.assertThat(sci.quantity()).isEqualTo(Quantity.of(1)),
                sci -> Assertions.assertThat(sci.available()).isTrue()
        );
    }

    @Test
    void givenInvalidQuantity_whenBrandNew_shouldThrowException() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();
        Quantity invalidQuantity = Quantity.ZERO;

        Assertions.assertThatThrownBy(() -> shoppingCartItem.changeQuantity(invalidQuantity))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenShoppingCartItem_whenTryToRefreshWithNullProduct_shouldThrowException() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Assertions.assertThatThrownBy(() -> shoppingCartItem.refresh(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void givenShoppingCartItem_whenTryToRefreshWithDifferentProductIds_shouldThrowException() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();
        Product productWithDifferentId = ProductTestDataBuilder.aProduct().build();

        Assertions.assertThatThrownBy(() -> shoppingCartItem.refresh(productWithDifferentId))
                .isInstanceOf(ShoppingCartItemIncompatibleProductException.class);
    }

    @Test
    void givenShoppingCartItem_whenUpdatePrice_shouldRecalculateTotal() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItem.brandNew()
                .shoppingCartId(new ShoppingCartId())
                .product(ProductTestDataBuilder.aProductAltMousePad().build())
                .quantity(Quantity.of(1))
                .build();

        Product productWithUpdatePrice = ProductTestDataBuilder.aProductAltMousePad()
                .price(Money.of("200.00"))
                .build();

        shoppingCartItem.refresh(productWithUpdatePrice);

        Assertions.assertWith(shoppingCartItem,
                sci -> Assertions.assertThat(sci.price()).isEqualTo(productWithUpdatePrice.price())
        );
    }

    @Test
    void givenShoppingCartItem_whenChangeQuantity_shouldRecalculateTotal() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem()
                .price(Money.of("1200.00"))
                .build();
        Quantity newQuantity = Quantity.of(5);

        shoppingCartItem.changeQuantity(newQuantity);

        Assertions.assertWith(shoppingCartItem,
                sci -> Assertions.assertThat(sci.quantity()).isEqualTo(newQuantity),
                sci -> Assertions.assertThat(sci.totalAmount()).isEqualTo(Money.of("6000.00"))
        );
    }

    @Test
    void givenShoppingCartItem_whenChangeAvailable_shouldUpdateStatus() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItem.brandNew()
                .shoppingCartId(new ShoppingCartId())
                .product(ProductTestDataBuilder.aProductAltMousePad().build())
                .quantity(Quantity.of(1))
                .build();

        Product productWithDifferentAvailable = ProductTestDataBuilder.aProductAltMousePad()
                .inStock(false)
                .build();

        shoppingCartItem.refresh(productWithDifferentAvailable);

        Assertions.assertThat(shoppingCartItem.available()).isFalse();
    }

    @Test
    void givenItemsWithDiffentIds_whenCompare_shouldNotBeEquals() {
        ShoppingCartItem shoppingCartItem1 = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();
        ShoppingCartItem shoppingCartItem2 = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Assertions.assertThat(shoppingCartItem1).isNotEqualTo(shoppingCartItem2);
        Assertions.assertThat(shoppingCartItem1.hashCode()).isNotEqualTo(shoppingCartItem2.hashCode());
    }

    @Test
    void givenItemsWithIdenticalIds_whenCompare_shouldBeEquals() {
        ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();
        ShoppingCartId shoppingCartId = new ShoppingCartId();
        ProductId productId = new ProductId();

        ShoppingCartItem shoppingCartItem1 = ShoppingCartItem.existing()
                .id(shoppingCartItemId)
                .shoppingCartId(shoppingCartId)
                .productId(productId)
                .productName(ProductName.of("Notebook"))
                .price(Money.of("1700.00"))
                .quantity(Quantity.of(1))
                .build();
        ShoppingCartItem shoppingCartItem2 = ShoppingCartItem.existing()
                .id(shoppingCartItemId)
                .shoppingCartId(shoppingCartId)
                .productId(productId)
                .productName(ProductName.of("Monitor 32 inch"))
                .price(Money.of("1110.00"))
                .quantity(Quantity.of(2))
                .build();

        Assertions.assertThat(shoppingCartItem1).isEqualTo(shoppingCartItem2);
        Assertions.assertThat(shoppingCartItem1.hashCode()).hasSameHashCodeAs(shoppingCartItem2.hashCode());
    }
}