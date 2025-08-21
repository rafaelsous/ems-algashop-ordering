package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartsPersistenceProvider;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        ShoppingCartsPersistenceProvider.class,
        ShoppingCartPersistenceAssembler.class,
        ShoppingCartPersistenceDisassembler.class,
        SpringDataAuditingConfig.class,
        HibernateConfig.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceAssembler.class,
        CustomerPersistenceDisassembler.class
})
class ShoppingCartsPersistenceProviderIT {
    private final ShoppingCartsPersistenceProvider shoppingCartsPersistenceProvider;
    private final ShoppingCartPersistenceRepository shoppingCartPersistenceRepository;
    private final CustomersPersistenceProvider customersPersistenceProvider;

    @Autowired
    public ShoppingCartsPersistenceProviderIT(ShoppingCartsPersistenceProvider shoppingCartsPersistenceProvider,
                                              ShoppingCartPersistenceRepository shoppingCartPersistenceRepository,
                                              CustomersPersistenceProvider customersPersistenceProvider) {
        this.shoppingCartsPersistenceProvider = shoppingCartsPersistenceProvider;
        this.shoppingCartPersistenceRepository = shoppingCartPersistenceRepository;
        this.customersPersistenceProvider = customersPersistenceProvider;
    }

    @BeforeEach
    void setUp() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldFindShoppingCartByCustomerId() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        shoppingCartsPersistenceProvider.add(shoppingCart);

        Optional<ShoppingCart> shoppingCartOptional = shoppingCartsPersistenceProvider.ofCustomer(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);

        assertThat(shoppingCartOptional).isPresent();
    }

    @Test
    void shouldRemoveByShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartId shoppingCartId = shoppingCart.id();

        shoppingCartsPersistenceProvider.add(shoppingCart);
        assertThat(shoppingCartsPersistenceProvider.ofId(shoppingCartId)).isPresent();

        shoppingCartsPersistenceProvider.remove(shoppingCart);
        assertThat(shoppingCartsPersistenceProvider.ofId(shoppingCartId)).isNotPresent();
    }

    @Test
    void shouldRemoveByShoppingCartId() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartId shoppingCartId = shoppingCart.id();

        shoppingCartsPersistenceProvider.add(shoppingCart);
        assertThat(shoppingCartsPersistenceProvider.ofId(shoppingCartId)).isPresent();

        shoppingCartsPersistenceProvider.remove(shoppingCartId);
        assertThat(shoppingCartsPersistenceProvider.ofId(shoppingCartId)).isNotPresent();
    }

    @Test
    void shouldVerifyIfExists() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartId shoppingCartId = shoppingCart.id();

        assertThat(shoppingCartsPersistenceProvider.exists(shoppingCartId)).isFalse();

        shoppingCartsPersistenceProvider.add(shoppingCart);

        assertThat(shoppingCartsPersistenceProvider.exists(shoppingCartId)).isTrue();
    }

    @Test
    void shouldUpdateAndKeepPersistenEntityState() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        UUID shoppingCartId = shoppingCart.id().value();
        Integer expectedTotalItems = shoppingCart.totalItems().value();

        shoppingCartsPersistenceProvider.add(shoppingCart);

        ShoppingCartPersistence shoppingCartPersistence = shoppingCartPersistenceRepository.findById(shoppingCartId).orElseThrow();

        assertThat(shoppingCartPersistence).satisfies(
                sc -> assertThat(sc.getTotalItems()).isEqualTo(expectedTotalItems),

                sc -> assertThat(sc.getCreatedByUserId()).isNotNull(),
                sc -> assertThat(sc.getLastModifiedByUserId()).isNotNull(),
                sc -> assertThat(sc.getLastModifiedAt()).isNotNull()
        );

        shoppingCart = shoppingCartsPersistenceProvider.ofId(shoppingCart.id()).orElseThrow();
        shoppingCart.empty();
        shoppingCartsPersistenceProvider.add(shoppingCart);

        shoppingCartPersistence = shoppingCartPersistenceRepository.findById(shoppingCartId).orElseThrow();

        assertThat(shoppingCartPersistence).satisfies(
                sc -> assertThat(sc.getItems()).isEmpty(),

                sc -> assertThat(sc.getCreatedByUserId()).isNotNull(),
                sc -> assertThat(sc.getLastModifiedByUserId()).isNotNull(),
                sc -> assertThat(sc.getLastModifiedAt()).isNotNull()
        );
    }

    @Test
    void shouldCountCorrectly() {
        assertThat(shoppingCartsPersistenceProvider.count()).isZero();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCartsPersistenceProvider.add(shoppingCart);

        assertThat(shoppingCartsPersistenceProvider.count()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateVersionCorrectly() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        UUID shoppingCartId = shoppingCart.id().value();

        shoppingCartsPersistenceProvider.add(shoppingCart);

        ShoppingCartPersistence shoppingCartPersistence = shoppingCartPersistenceRepository.findById(shoppingCartId).orElseThrow();

        assertThat(shoppingCartPersistence.getVersion()).isZero();

        shoppingCart = shoppingCartsPersistenceProvider.ofId(shoppingCart.id()).orElseThrow();
        shoppingCart.empty();
        shoppingCartsPersistenceProvider.add(shoppingCart);

        shoppingCartPersistence = shoppingCartPersistenceRepository.findById(shoppingCartId).orElseThrow();

        assertThat(shoppingCartPersistence.getVersion()).isEqualTo(1);
    }
}