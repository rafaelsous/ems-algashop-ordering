package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.core.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.AbstractInfrastructureIT;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.*;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomersPersistenceProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration,classpath:db/testdata")
class ShoppingCartsPersistenceProviderIT extends AbstractInfrastructureIT {
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

    @Test
    void shouldFindShoppingCartByCustomerId() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customersPersistenceProvider.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();

        shoppingCartsPersistenceProvider.add(shoppingCart);

        Optional<ShoppingCart> shoppingCartOptional = shoppingCartsPersistenceProvider.ofCustomer(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);

        assertThat(shoppingCartOptional).isPresent();
    }

    @Test
    void shouldRemoveByShoppingCart() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customersPersistenceProvider.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        ShoppingCartId shoppingCartId = shoppingCart.id();

        shoppingCartsPersistenceProvider.add(shoppingCart);
        assertThat(shoppingCartsPersistenceProvider.ofId(shoppingCartId)).isPresent();

        shoppingCartsPersistenceProvider.remove(shoppingCart);
        assertThat(shoppingCartsPersistenceProvider.ofId(shoppingCartId)).isNotPresent();
    }

    @Test
    void shouldRemoveByShoppingCartId() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customersPersistenceProvider.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        ShoppingCartId shoppingCartId = shoppingCart.id();

        shoppingCartsPersistenceProvider.add(shoppingCart);
        assertThat(shoppingCartsPersistenceProvider.ofId(shoppingCartId)).isPresent();

        shoppingCartsPersistenceProvider.remove(shoppingCartId);
        assertThat(shoppingCartsPersistenceProvider.ofId(shoppingCartId)).isNotPresent();
    }

    @Test
    void shouldVerifyIfExists() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customersPersistenceProvider.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        ShoppingCartId shoppingCartId = shoppingCart.id();

        assertThat(shoppingCartsPersistenceProvider.exists(shoppingCartId)).isFalse();

        shoppingCartsPersistenceProvider.add(shoppingCart);

        assertThat(shoppingCartsPersistenceProvider.exists(shoppingCartId)).isTrue();
    }

    @Test
    void shouldUpdateAndKeepPersistenEntityState() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customersPersistenceProvider.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        UUID shoppingCartId = shoppingCart.id().value();

        shoppingCartsPersistenceProvider.add(shoppingCart);

        ShoppingCartPersistence shoppingCartPersistence = shoppingCartPersistenceRepository.findById(shoppingCartId).orElseThrow();
        Integer expectedTotalItems = shoppingCart.totalItems().value();

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
        long beforeCount = shoppingCartsPersistenceProvider.count();

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customersPersistenceProvider.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        shoppingCartsPersistenceProvider.add(shoppingCart);

        long afterCount = shoppingCartsPersistenceProvider.count();
        assertThat(afterCount).isEqualTo(beforeCount + 1);
    }

    @Test
    void shouldUpdateVersionCorrectly() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customersPersistenceProvider.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
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