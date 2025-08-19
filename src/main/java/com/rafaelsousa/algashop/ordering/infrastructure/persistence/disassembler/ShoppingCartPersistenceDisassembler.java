package com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistence;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceDisassembler {

    public ShoppingCart toDomain(ShoppingCartPersistence shoppingCartPersistence) {
        return ShoppingCart.existing()
                .id(new ShoppingCartId(shoppingCartPersistence.getId()))
                .customerId(new CustomerId(shoppingCartPersistence.getCustomerId()))
                .totalItems(Quantity.of(shoppingCartPersistence.getTotalItems()))
                .totalAmount(Money.of(shoppingCartPersistence.getTotalAmount()))
                .createdAt(shoppingCartPersistence.getCreatedAt())
                .items(
                        buildItems(shoppingCartPersistence.getItems())
                )
                .build();
    }

    private Set<ShoppingCartItem> buildItems(Set<ShoppingCartItemPersistence> items) {
        if (Objects.isNull(items)) return new HashSet<>();

        return items.stream().map(this::buildItem).collect(Collectors.toSet());
    }

    private ShoppingCartItem buildItem(ShoppingCartItemPersistence shoppingCartItemPersistence) {
        return ShoppingCartItem.existing()
                .id(new ShoppingCartItemId(shoppingCartItemPersistence.getId()))
                .shoppingCartId(new ShoppingCartId(shoppingCartItemPersistence.getShoppingCardId()))
                .productId(new ProductId(shoppingCartItemPersistence.getProductId()))
                .productName(ProductName.of(shoppingCartItemPersistence.getProductName()))
                .price(Money.of(shoppingCartItemPersistence.getPrice()))
                .available(shoppingCartItemPersistence.getAvailable())
                .quantity(Quantity.of(shoppingCartItemPersistence.getQuantity()))
                .totalAmount(Money.of(shoppingCartItemPersistence.getTotalAmount()))
                .build();
    }
}