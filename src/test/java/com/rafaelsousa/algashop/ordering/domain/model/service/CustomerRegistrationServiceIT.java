package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerRegistrationServiceIT {
    private final CustomerRegistrationService customerRegistrationService;

    @Autowired
    CustomerRegistrationServiceIT(CustomerRegistrationService customerRegistrationService) {
        this.customerRegistrationService = customerRegistrationService;
    }

    @Test
    void shouldRegister() {
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