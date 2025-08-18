package com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShoppingCartPersistenceAssemblerTest {

    @Mock
    private CustomerPersistenceRepository customerPersistenceRepository;

    @InjectMocks
    private ShoppingCartPersistenceAssembler assembler;

    @BeforeEach
    void setUp() {
        Mockito.when(customerPersistenceRepository.getReferenceById(Mockito.any(UUID.class)))
                .then(a -> {
                    UUID customerId = a.getArgument(0);

                    return CustomerPersistenceTestDataBuilder.aCustomer().id(customerId).build();
                });
    }

    @Test
    void shouldMapToPersistenceEntity() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        ShoppingCartPersistence shoppingCartPersistence = assembler.fromDomain(shoppingCart);

        assertThat(shoppingCartPersistence).satisfies(
                scp -> {
                    assertThat(scp.getId()).isEqualTo(shoppingCart.id().value());
                    assertThat(scp.getCustomer().getId()).isEqualTo(shoppingCart.customerId().value());
                    assertThat(scp.getTotalItems()).isEqualTo(shoppingCart.totalItems().value());
                    assertThat(scp.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value());
                    assertThat(scp.getCreatedAt()).isEqualTo(shoppingCart.createdAt());
                    assertThat(scp.getVersion()).isEqualTo(shoppingCart.version());
                    assertThat(scp.getItems()).hasSize(shoppingCart.items().size());
                }
        );
    }

    @Test
    void shouldMergeItems() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartPersistence shoppingCartPersistence = ShoppingCartPersistenceTestDataBuilder
                .existingShoppingCart().build();

        Set<ShoppingCartItem> items = shoppingCart.items();
        Set<ShoppingCartItemPersistence> itemPersistenceSet = items.stream()
                .map(assembler::fromDomain).collect(Collectors.toSet());

        assertThat(itemPersistenceSet).doesNotHaveSameHashCodeAs(shoppingCartPersistence.getItems());

        assembler.merge(shoppingCartPersistence, shoppingCart);

        assertThat(itemPersistenceSet).hasSameHashCodeAs(shoppingCartPersistence.getItems());
    }
}