package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartOutput;
import com.rafaelsousa.algashop.ordering.core.ports.out.shoppingcart.ForObtainingShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ForObtainingShoppingCartsJpaRepositoryImpl implements ForObtainingShoppingCarts {
    private final ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;
    private final Mapper mapper;

    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        ShoppingCartPersistence shoppingCartPersistence = shoppingCartPersistenceRepository.findById(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(new ShoppingCartId(shoppingCartId)));

        return mapper.convert(shoppingCartPersistence, ShoppingCartOutput.class);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        ShoppingCartPersistence shoppingCartPersistence = shoppingCartPersistenceRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(new CustomerId(customerId)));

        return mapper.convert(shoppingCartPersistence, ShoppingCartOutput.class);
    }
}