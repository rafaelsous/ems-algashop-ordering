package com.rafaelsousa.algashop.ordering.core.application.customer;

import com.rafaelsousa.algashop.ordering.core.application.AbstractApplicationIT;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Email;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.FullName;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.*;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerFilter;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForQueryingCustomers;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerSummaryOutput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class CustomerQueryServiceIT extends AbstractApplicationIT {
    private final Customers customers;
    private final ForQueryingCustomers customerQueryService;

    @Autowired
    CustomerQueryServiceIT(Customers customers, ForQueryingCustomers customerQueryService) {
        this.customers = customers;
        this.customerQueryService = customerQueryService;
    }

    @Test
    void shouldFindById() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        CustomerOutput customerOutput = customerQueryService.findById(customer.id().value());

        assertThat(customerOutput)
                .extracting("id", "firstName", "lastName", "document", "email", "phone")
                .containsExactly(
                        customer.id().value(),
                        customer.fullName().firstName(),
                        customer.fullName().lastName(),
                        customer.document().value(),
                        customer.email().value(),
                        customer.phone().value()
                );
    }

    @Test
    void shouldThrowExceptioWhenTryingNonExistentCustomer() {
        UUID rawNonExistentCustomerId = new CustomerId().value();

        assertThatThrownBy(() -> customerQueryService.findById(rawNonExistentCustomerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void shouldFilterByFirstName() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Alan", "Kardec")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Leonardo", "DiCaprio")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Mike", "Tyson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Alan", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Rachel", "Smith")).build());

        CustomerFilter filter = CustomerFilter.ofDefault();
        filter.setFirstName("Alan");

        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();

        assertThat(page.getContent())
                .hasSize(2)
                .extracting(CustomerSummaryOutput::getFirstName, CustomerSummaryOutput::getLastName)
                .containsExactlyInAnyOrder(
                        tuple("Alan", "Kardec"),
                        tuple("Alan", "Johnson")
                );
    }

    @Test
    void shouldFilterByEmail() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().email(Email.of("alan.kardec@email.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().email(Email.of("leonardo.dicaprio@email.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().email(Email.of("mike.tyson@email.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().email(Email.of("alan.johnson@email.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().email(Email.of("rachel.smith@email.com")).build());

        CustomerFilter filter = CustomerFilter.ofDefault();
        filter.setEmail("rachel.smith@email.com");

        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();

        assertThat(page.getContent())
                .hasSize(1)
                .extracting(CustomerSummaryOutput::getEmail)
                .containsExactly("rachel.smith@email.com");
    }

    @Test
    void shouldFilterByFirstNameAndEmail() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer()
                .fullName(FullName.of("Alan", "Kardec")).email(Email.of("alan.kardec@email.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer()
                .fullName(FullName.of("Leonardo", "DiCaprio")).email(Email.of("leonardo.dicaprio@email.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer()
                .fullName(FullName.of("Mike", "Tyson")).email(Email.of("mike.tyson@email.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer()
                .fullName(FullName.of("Alan", "Johnson")).email(Email.of("alan.johnson@email.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer()
                .fullName(FullName.of("Rachel", "Smith")).email(Email.of("rachel.smith@email.com")).build());

        CustomerFilter filter = CustomerFilter.ofDefault();
        filter.setFirstName("Alan");
        filter.setEmail("alan.kardec@email.com");

        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();

        assertThat(page.getContent())
                .hasSize(1)
                .extracting(CustomerSummaryOutput::getFirstName, CustomerSummaryOutput::getEmail)
                .containsExactly(
                        tuple("Alan", "alan.kardec@email.com")
                );
    }

    @Test
    void shouldFilterByPage() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Alan", "Kardec")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Leonardo", "DiCaprio")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Mike", "Tyson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Alan", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(FullName.of("Rachel", "Smith")).build());

        CustomerFilter filter = CustomerFilter.of(2, 0);
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
    }

    @Test
    void shouldSortByRegisteredAt() {
        OffsetDateTime currentDateTime = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Alan", "Kardec")).registeredAt(currentDateTime.minusDays(7)).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Leonardo", "DiCaprio")).registeredAt(currentDateTime.minusDays(6)).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Mike", "Tyson")).registeredAt(currentDateTime.minusDays(6)).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Alan", "Johnson")).registeredAt(currentDateTime).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Rachel", "Smith")).registeredAt(currentDateTime).build());

        CustomerFilter filter = CustomerFilter.ofDefault();
        filter.setSortByProperty(CustomerFilter.SortType.REGISTERED_AT);
        filter.setSortDirection(Sort.Direction.ASC);
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        assertThat(page.getContent().getFirst().getRegisteredAt()).isEqualTo(currentDateTime.minusDays(7));
        assertThat(page.getContent().getLast().getRegisteredAt()).isEqualTo(currentDateTime);

        filter.setSortDirection(Sort.Direction.DESC);
        page = customerQueryService.filter(filter);

        assertThat(page.getContent().getFirst().getRegisteredAt()).isEqualTo(currentDateTime);
        assertThat(page.getContent().getLast().getRegisteredAt()).isEqualTo(currentDateTime.minusDays(7));
    }

    @Test
    void shouldSortByFirstName() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Brad", "Pitt")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Leonardo", "DiCaprio")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Mike", "Tyson")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Alan", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                .fullName(FullName.of("Rachel", "Smith")).build());

        CustomerFilter filter = CustomerFilter.ofDefault();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.ASC);
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        assertThat(page.getContent().getFirst().getFirstName()).isEqualToIgnoringCase("Alan");
        assertThat(page.getContent().getLast().getFirstName()).isEqualToIgnoringCase("Rachel");

        filter.setSortDirection(Sort.Direction.DESC);
        page = customerQueryService.filter(filter);

        assertThat(page.getContent().getFirst().getFirstName()).isEqualToIgnoringCase("Rachel");
        assertThat(page.getContent().getLast().getFirstName()).isEqualToIgnoringCase("Alan");
    }

    @Test
    void givenInvalidEmail_whenFilter_shouldReturnEmptyPage() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        CustomerFilter filter = CustomerFilter.ofDefault();
        filter.setEmail("invalid-email");

        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        assertThat(page.isEmpty()).isTrue();
    }
}