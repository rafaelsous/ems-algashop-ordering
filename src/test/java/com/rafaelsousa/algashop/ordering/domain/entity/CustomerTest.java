package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.utils.IdGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        UUID id = IdGenerator.generateTimeBasedUUID();
        LocalDate birthDate = LocalDate.of(1990, 9, 18);
        OffsetDateTime now = OffsetDateTime.now();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        new Customer(
                                id,
                                "John Doe",
                                birthDate,
                                "invalid_email",
                                "123-456-7890",
                                "123-45-6789",
                                false,
                                now
                        )
                );
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        Customer customer = new Customer(
                IdGenerator.generateTimeBasedUUID(),
                "John Doe",
                LocalDate.of(1990, 9, 18),
                "john.doe@email.com",
                "123-456-7890",
                "123-45-6789",
                false,
                OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.changeEmail("invalid_email"));
    }
}