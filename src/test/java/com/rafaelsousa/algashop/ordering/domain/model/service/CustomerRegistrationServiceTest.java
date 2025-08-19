package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.repository.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    @Mock
    private Customers customers;

    @InjectMocks
    private CustomerRegistrationService customerRegistrationService;

    @Test
    void shouldRegister() {
        when(customers.isEmailUnique(any(Email.class), any(CustomerId.class))).thenReturn(true);

        Customer customer = customerRegistrationService.register(
                FullName.of("John", "Doe"),
                BirthDate.of(LocalDate.of(1990, 1, 1)),
                Email.of("john.doe@email.com"),
                Phone.of("123-456-7890"),
                Document.of("123-45-6789"),
                true,
                Address.builder()
                        .street("Bourbon Street")
                        .number("2256")
                        .neighborhood("North Ville")
                        .city("Yostfort")
                        .state("South Carolina")
                        .zipCode(ZipCode.of("12345"))
                        .complement("Room 722")
                        .build()
        );

        assertThat(customer.fullName()).isEqualTo(FullName.of("John", "Doe"));
        assertThat(customer.birthDate()).isEqualTo(BirthDate.of(LocalDate.of(1990, 1, 1)));
    }
}