package com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceAssembler {
    private final CustomerPersistenceRepository customerPersistenceRepository;

    public ShoppingCartPersistence fromDomain(ShoppingCart aggregateRoot) {
        return merge(new ShoppingCartPersistence(), aggregateRoot);
    }

    public ShoppingCartPersistence merge(ShoppingCartPersistence shoppingCartPersistence, ShoppingCart shoppingCart) {
        shoppingCartPersistence.setId(shoppingCart.id().value());
        shoppingCartPersistence.setTotalItems(shoppingCart.totalItems().value());
        shoppingCartPersistence.setTotalAmount(shoppingCart.totalAmount().value());
        shoppingCartPersistence.setCreatedAt(shoppingCart.createdAt());
        shoppingCartPersistence.setVersion(shoppingCart.version());

        CustomerPersistence customerPersistence = customerPersistenceRepository.getReferenceById(shoppingCart.customerId().value());
        shoppingCartPersistence.setCustomer(customerPersistence);

        Set<ShoppingCartItemPersistence> mergedItems = mergeItems(shoppingCartPersistence, shoppingCart);
        shoppingCartPersistence.replaceItems(mergedItems);

        return shoppingCartPersistence;
    }

    public ShoppingCartItemPersistence fromDomain(ShoppingCartItem shoppingCartItem) {
        return merge(new ShoppingCartItemPersistence(), shoppingCartItem);
    }

    private Set<ShoppingCartItemPersistence> mergeItems(ShoppingCartPersistence shoppingCartPersistence,
                                                 ShoppingCart shoppingCart) {
        Set<ShoppingCartItem> newOrUpdatedItems = shoppingCart.items();

        if (CollectionUtils.isEmpty(newOrUpdatedItems)) {
            return new HashSet<>();
        }

        Set<ShoppingCartItemPersistence> existingItems = shoppingCartPersistence.getItems();
        if (CollectionUtils.isEmpty(existingItems)) {
            return newOrUpdatedItems.stream()
                    .map(this::fromDomain)
                    .collect(Collectors.toSet());
        }

        Map<UUID, ShoppingCartItemPersistence> existingItemsMap = existingItems.stream()
                .collect(Collectors.toMap(ShoppingCartItemPersistence::getId, item -> item));

        return newOrUpdatedItems.stream()
                .map(orderItem -> {
                    ShoppingCartItemPersistence itemPersistence = existingItemsMap
                            .getOrDefault(orderItem.id().value(), new ShoppingCartItemPersistence());

                    return merge(itemPersistence, orderItem);
                }).collect(Collectors.toSet());
    }

    private ShoppingCartItemPersistence merge(ShoppingCartItemPersistence shoppingCartItemPersistence,
                                       ShoppingCartItem shoppingCartItem) {
        shoppingCartItemPersistence.setId(shoppingCartItem.id().value());
        shoppingCartItemPersistence.setProductId(shoppingCartItem.productId().value());
        shoppingCartItemPersistence.setProductName(shoppingCartItem.productName().value());
        shoppingCartItemPersistence.setPrice(shoppingCartItem.price().value());
        shoppingCartItemPersistence.setQuantity(shoppingCartItem.quantity().value());
        shoppingCartItemPersistence.setTotalAmount(shoppingCartItem.totalAmount().value());

        return shoppingCartItemPersistence;
    }
}