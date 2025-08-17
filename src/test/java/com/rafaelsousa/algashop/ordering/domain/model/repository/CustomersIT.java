package com.rafaelsousa.algashop.ordering.domain.model.repository;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Email;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.FullName;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Phone;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceAssembler.class,
        CustomerPersistenceDisassembler.class,
        HibernateConfig.class
})
class CustomersIT {
    private final Customers customers;

    @Autowired
    public CustomersIT(Customers customers) {
        this.customers = customers;
    }

    @Test
    void shouldPersistAndFind() {
        Customer originalCustomer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = originalCustomer.id();

        customers.add(originalCustomer);
        Optional<Customer> customerOptional = customers.ofId(customerId);

        assertThat(customerOptional).isPresent();

        Customer savedCustomer = customerOptional.get();

        assertThat(savedCustomer).satisfies(
                sc -> assertThat(sc.id()).isEqualTo(customerId),
                sc -> assertThat(sc.fullName()).isEqualTo(originalCustomer.fullName()),
                sc -> assertThat(sc.birthDate()).isEqualTo(originalCustomer.birthDate()),
                sc -> assertThat(sc.email()).isEqualTo(originalCustomer.email()),
                sc -> assertThat(sc.phone()).isEqualTo(originalCustomer.phone()),
                sc -> assertThat(sc.document()).isEqualTo(originalCustomer.document()),

                sc -> assertThat(sc.isPromotionNotificationsAllowed()).isFalse(),
                sc -> assertThat(sc.isArchived()).isFalse(),
                sc -> assertThat(sc.registeredAt()).isNotNull(),
                sc -> assertThat(sc.archivedAt()).isNull(),
                sc -> assertThat(sc.loyaltyPoints().value()).isZero()
        );
    }

    @Test
    void shouldUpdateExistingCustomer() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        CustomerId customerId = customer.id();
        FullName updateFullName = FullName.of("Leonardo", "DiCaprio");

        customers.add(customer);

        customer = customers.ofId(customerId).orElseThrow();

        customer.changeName(updateFullName);

        customers.add(customer);

        customer = customers.ofId(customerId).orElseThrow();

        assertThat(customer.fullName()).isEqualTo(updateFullName);
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        CustomerId customerId = customer.id();
        Phone originPhone = customer.phone();

        customers.add(customer);

        Customer customerT1 = customers.ofId(customerId).orElseThrow();
        Customer customerT2 = customers.ofId(customerId).orElseThrow();

        Email newEmail = Email.of("other-email@example.com");
        customerT1.changeEmail(newEmail);
        customers.add(customerT1);

        Phone newPhone = Phone.of("456-789-1234");
        customerT2.changePhone(newPhone);

        assertThatThrownBy(() -> customers.add(customerT2)).isInstanceOf(ObjectOptimisticLockingFailureException.class);

        Customer savedCustomer = customers.ofId(customerId).orElseThrow();

        assertThat(savedCustomer).satisfies(
                sc -> assertThat(sc.email()).isEqualTo(newEmail),
                sc -> assertThat(sc.phone()).isEqualTo(originPhone)
        );
    }

    @Test
    void shouldCountExistingCustomers() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        assertThat(customers.count()).isZero();

        customers.add(customer);

        assertThat(customers.count()).isEqualTo(1L);
    }

    @Test
    void shouldReturnIfCustomerExists() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        assertThat(customers.exists(customer.id())).isFalse();

        customers.add(customer);

        assertThat(customers.exists(customer.id())).isTrue();
        assertThat(customers.exists(new CustomerId())).isFalse();
    }

    @Test
    void shouldFindByEmail() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customers.add(customer);

        assertThat(customers.ofEmail(customer.email())).isPresent();
    }

    @Test
    void shouldNotFindByEmailIfNoCustomerExistsWithEmail() {
        String inexistingEmail = "%s@example.com".formatted(UUID.randomUUID());

        assertThat(customers.ofEmail(Email.of(inexistingEmail))).isNotPresent();
    }

    @Test
    void shouldReturnIfEmailIsInUser() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customers.add(customer);

        assertThat(customers.isEmailUnique(customer.email(), customer.id())).isTrue();
        assertThat(customers.isEmailUnique(customer.email(), new CustomerId())).isFalse();
        assertThat(customers.isEmailUnique(Email.of("other-email@example.com"), customer.id())).isTrue();
        assertThat(customers.isEmailUnique(Email.of("other-email@example.com"), new CustomerId())).isTrue();
    }
}