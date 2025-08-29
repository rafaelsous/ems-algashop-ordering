package com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.rafaelsousa.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.rafaelsousa.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.rafaelsousa.algashop.ordering.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {
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