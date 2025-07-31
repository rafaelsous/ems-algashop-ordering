package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.exception.CustomerArchivedException;
import com.rafaelsousa.algashop.ordering.domain.utils.IdGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
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

        customer.archive();

        Assertions.assertWith(customer,
                c -> assertThat(c.fullName()).isEqualTo("Anonymous"),
                c -> assertThat(c.birthDate()).isNull(),
                c -> assertThat(c.email()).isNotEqualTo("john.doe@email.com"),
                c -> assertThat(c.phone()).isEqualTo("000-000-0000"),
                c -> assertThat(c.document()).isEqualTo("000-00-0000"),
                c -> assertThat(c.archivedAt()).isNotNull(),
                c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse()
        );
    }

    @Test
    void given_archivedCustomer_whenTryUpdate_shouldGenerateException() {
        Customer customer = new Customer(
                IdGenerator.generateTimeBasedUUID(),
                "Anonymous",
                null,
                UUID.randomUUID().toString().concat("@anonymous.com"),
                "000-000-0000",
                "000-00-0000",
                false,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                10
        );

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeEmail("example@email.com"));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changePhone("000-000-0000"));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enablePromotionNotifications);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disablePromotionNotifications);
    }
}