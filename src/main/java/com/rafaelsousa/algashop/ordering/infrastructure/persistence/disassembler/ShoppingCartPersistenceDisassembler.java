package com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
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

        return items.stream()
                .map(shoppingCartItemPersistence -> ShoppingCartItem.brandNew()
                        .shoppingCartId(new ShoppingCartId(shoppingCartItemPersistence.getShoppingCardId()))
                        .product(Product.builder()
                                .id(new ProductId(shoppingCartItemPersistence.getProductId()))
                                .name(ProductName.of(shoppingCartItemPersistence.getProductName()))
                                .price(Money.of(shoppingCartItemPersistence.getPrice()))
                                .build()
                        )
                        .quantity(Quantity.of(shoppingCartItemPersistence.getQuantity()))
                        .build())
                .collect(Collectors.toSet());
    }
}