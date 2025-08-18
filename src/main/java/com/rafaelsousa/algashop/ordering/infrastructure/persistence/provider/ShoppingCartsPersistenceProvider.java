package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartsPersistenceProvider implements ShoppingCarts {
    private final ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;
    private final ShoppingCartPersistenceAssembler assembler;
    private final ShoppingCartPersistenceDisassembler disassembler;

    @Override
    public Optional<ShoppingCart> ofCustomer(CustomerId customerId) {
        Optional<ShoppingCartPersistence> shoppingCartPersistenceOptional = shoppingCartPersistenceRepository
                .findByCustomerId(customerId.value());

        return shoppingCartPersistenceOptional.map(disassembler::toDomain);
    }

    @Override
    public void remove(ShoppingCart shoppingCart) {
        shoppingCartPersistenceRepository.deleteById(shoppingCart.id().value());
    }

    @Override
    public void remove(ShoppingCartId shoppingCartId) {
        shoppingCartPersistenceRepository.deleteById(shoppingCartId.value());
    }

    @Override
    public Optional<ShoppingCart> ofId(ShoppingCartId shoppingCartId) {
        Optional<ShoppingCartPersistence> shoppingCartPersistenceOptional = shoppingCartPersistenceRepository
                .findById(shoppingCartId.value());

        return shoppingCartPersistenceOptional.map(disassembler::toDomain);
    }

    @Override
    public boolean exists(ShoppingCartId shoppingCartId) {
        return shoppingCartPersistenceRepository.existsById(shoppingCartId.value());
    }

    @Override
    public void add(ShoppingCart aggregateRoot) {
        UUID shoppingCartId = aggregateRoot.id().value();

        shoppingCartPersistenceRepository.findById(shoppingCartId).ifPresentOrElse(
                shoppingCartPersistence -> this.update(aggregateRoot, shoppingCartPersistence),
                () -> this.insert(aggregateRoot)
        );
    }

    @Override
    public long count() {
        return shoppingCartPersistenceRepository.count();
    }

    private void insert(ShoppingCart aggregateRoot) {
        ShoppingCartPersistence shoppingCartPersistence = assembler.fromDomain(aggregateRoot);

        shoppingCartPersistenceRepository.saveAndFlush(shoppingCartPersistence);

        this.updateVersion(aggregateRoot, new ShoppingCartPersistence());
    }

    private void update(ShoppingCart aggregateRoot, ShoppingCartPersistence shoppingCartPersistence) {
        shoppingCartPersistence = assembler.merge(shoppingCartPersistence, aggregateRoot);

        shoppingCartPersistenceRepository.saveAndFlush(shoppingCartPersistence);

        this.updateVersion(aggregateRoot, shoppingCartPersistence);
    }

    @SneakyThrows
    @SuppressWarnings("java:S3011")
    private void updateVersion(ShoppingCart aggregateRoot, ShoppingCartPersistence customerPersistence) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);

        ReflectionUtils.setField(version, aggregateRoot, customerPersistence.getVersion());

        version.setAccessible(false);
    }
}