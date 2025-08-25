package com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
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

        Set<ShoppingCartItemPersistence> mergedItems = buildItems(shoppingCart.items());
        shoppingCartPersistence.replaceItems(mergedItems);

        shoppingCartPersistence.addEvents(shoppingCart.domainEvents());

        return shoppingCartPersistence;
    }

    public ShoppingCartItemPersistence fromDomain(ShoppingCartItem shoppingCartItem) {
        return merge(new ShoppingCartItemPersistence(), shoppingCartItem);
    }

    private Set<ShoppingCartItemPersistence> buildItems(Set<ShoppingCartItem> items) {
        return items.stream().map(i -> this.merge(new ShoppingCartItemPersistence(), i)).collect(Collectors.toSet());
    }

    private ShoppingCartItemPersistence merge(ShoppingCartItemPersistence shoppingCartItemPersistence,
                                       ShoppingCartItem shoppingCartItem) {
        shoppingCartItemPersistence.setId(shoppingCartItem.id().value());
        shoppingCartItemPersistence.setProductId(shoppingCartItem.productId().value());
        shoppingCartItemPersistence.setProductName(shoppingCartItem.productName().value());
        shoppingCartItemPersistence.setPrice(shoppingCartItem.price().value());
        shoppingCartItemPersistence.setQuantity(shoppingCartItem.quantity().value());
        shoppingCartItemPersistence.setTotalAmount(shoppingCartItem.totalAmount().value());
        shoppingCartItemPersistence.setAvailable(shoppingCartItem.isAvailable());

        return shoppingCartItemPersistence;
    }
}