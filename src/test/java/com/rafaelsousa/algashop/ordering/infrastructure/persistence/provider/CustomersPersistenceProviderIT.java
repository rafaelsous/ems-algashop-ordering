package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Email;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceAssembler.class,
        CustomerPersistenceDisassembler.class,
        SpringDataAuditingConfig.class,
        HibernateConfig.class
})
class CustomersPersistenceProviderIT {
    private final CustomersPersistenceProvider customersPersistenceProvider;
    private final CustomerPersistenceRepository customerPersistenceRepository;

    @Autowired
    CustomersPersistenceProviderIT(CustomersPersistenceProvider customersPersistenceProvider, CustomerPersistenceRepository customerPersistenceRepository) {
        this.customersPersistenceProvider = customersPersistenceProvider;
        this.customerPersistenceRepository = customerPersistenceRepository;
    }

    @Test
    void shouldUpdateAndKeepPersistenceEntityState() {
        String originEmail = "user@email.com";
        Customer customer = CustomerTestDataBuilder.existingCustomer()
                .email(Email.of(originEmail))
                .build();
        UUID customerId = customer.id().value();

        customersPersistenceProvider.add(customer);

        CustomerPersistence customerPersistence = customerPersistenceRepository.findById(customerId).orElseThrow();

        assertThat(customerPersistence).satisfies(
                cp -> assertThat(cp.getEmail()).isEqualTo(originEmail),

                cp -> assertThat(cp.getCreatedByUserId()).isNotNull(),
                cp -> assertThat(cp.getLastModifiedByUserId()).isNotNull(),
                cp -> assertThat(cp.getLastModifiedAt()).isNotNull()
        );

        String newEmail = "new@email.com";

        customer = customersPersistenceProvider.ofId(customer.id()).orElseThrow();
        customer.changeEmail(Email.of(newEmail));
        customersPersistenceProvider.add(customer);

        customerPersistence = customerPersistenceRepository.findById(customerId).orElseThrow();

        assertThat(customerPersistence).satisfies(
                cp -> assertThat(cp.getEmail()).isEqualTo(newEmail),

                cp -> assertThat(cp.getCreatedByUserId()).isNotNull(),
                cp -> assertThat(cp.getLastModifiedByUserId()).isNotNull(),
                cp -> assertThat(cp.getLastModifiedAt()).isNotNull()
        );
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldAddAndFindNotFailWhenNoTransaction() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customersPersistenceProvider.add(customer);

        assertThatNoException().isThrownBy(() -> customersPersistenceProvider.ofId(customer.id()).orElseThrow());
    }

    @Test
    void shouldCountCorrectly() {
        assertThat(customersPersistenceProvider.count()).isZero();

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customersPersistenceProvider.add(customer);

        assertThat(customersPersistenceProvider.count()).isEqualTo(1L);
    }

    @Test
    void shouldVerifyIfExists() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        CustomerId customerId = customer.id();

        assertThat(customersPersistenceProvider.exists(customerId)).isFalse();

        customersPersistenceProvider.add(customer);

        assertThat(customersPersistenceProvider.exists(customerId)).isTrue();
    }

    @Test
    void shouldUpdateVersionCorrectly() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        CustomerId customerId = customer.id();

        customersPersistenceProvider.add(customer);

        CustomerPersistence customerPersistence = customerPersistenceRepository.findById(customerId.value()).orElseThrow();

        assertThat(customerPersistence.getVersion()).isZero();

        customer.changeEmail(Email.of("other-email@example.com"));
        customersPersistenceProvider.add(customer);

        customerPersistence = customerPersistenceRepository.findById(customerId.value()).orElseThrow();

        assertThat(customerPersistence.getVersion()).isEqualTo(1);
    }
}