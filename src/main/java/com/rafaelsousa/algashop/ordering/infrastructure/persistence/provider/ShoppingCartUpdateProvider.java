package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.service.ShoppingCartProductAdjustmentService;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ShoppingCartUpdateProvider implements ShoppingCartProductAdjustmentService {
    private final ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;

    @Override
    @Transactional
    public void adjustPrice(ProductId productId, Money updatedPrice) {
        shoppingCartPersistenceRepository.updateItemPrice(productId.value(), updatedPrice.value());
        shoppingCartPersistenceRepository.recalculateTotalForCartsWithProduct(productId.value());
    }

    @Override
    @Transactional
    public void changeAvailability(ProductId productId, boolean available) {
        shoppingCartPersistenceRepository.updateItemAvailability(productId.value(), available);
    }
}