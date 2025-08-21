package com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler;

import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShoppingCartPersistenceDisassemblerTest {

    @InjectMocks
    private ShoppingCartPersistenceDisassembler disassembler;

    @Test
    void shouldMapToDomain() {
        ShoppingCartPersistence shoppingCartPersistence = ShoppingCartPersistenceTestDataBuilder
                .existingShoppingCart().build();

        ShoppingCart shoppingCart = disassembler.toDomain(shoppingCartPersistence);

        assertThat(shoppingCart).satisfies(
                sc -> {
                    assertThat(sc.id().value()).isEqualTo(shoppingCartPersistence.getId());
                    assertThat(sc.customerId()).isEqualTo(new CustomerId(shoppingCartPersistence.getCustomerId()));
                    assertThat(sc.totalItems()).isEqualTo(Quantity.of(shoppingCartPersistence.getTotalItems()));
                    assertThat(sc.totalAmount()).isEqualTo(Money.of(shoppingCartPersistence.getTotalAmount()));
                    assertThat(sc.createdAt()).isEqualTo(shoppingCartPersistence.getCreatedAt());
                    assertThat(sc.items()).hasSize(shoppingCartPersistence.getItems().size());
                }
        );
    }
}