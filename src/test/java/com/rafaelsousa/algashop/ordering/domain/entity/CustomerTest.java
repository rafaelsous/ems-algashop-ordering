package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.exception.CustomerArchivedException;
import com.rafaelsousa.algashop.ordering.domain.valueobject.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.valueobject.FullName;
import com.rafaelsousa.algashop.ordering.domain.valueobject.LoyaltyPoints;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        CustomerId id = new CustomerId();
        FullName fullName = new FullName("John", "Doe");
        LocalDate birthDate = LocalDate.of(1990, 9, 18);
        OffsetDateTime now = OffsetDateTime.now();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        new Customer(
                                id,
                                fullName,
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
                new CustomerId(),
                new FullName("John", "Doe"),
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
                new CustomerId(),
                new FullName("John", "Doe"),
                LocalDate.of(1990, 9, 18),
                "john.doe@email.com",
                "123-456-7890",
                "123-45-6789",
                false,
                OffsetDateTime.now()
        );

        customer.archive();

        FullName fullNameAnonymous = new FullName("Anonymous", "Anonymous");

        Assertions.assertWith(customer,
                c -> assertThat(c.fullName()).isEqualTo(fullNameAnonymous),
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
                new CustomerId(),
                new FullName("Anonymous", "Anonymous"),
                null,
                UUID.randomUUID().toString().concat("@anonymous.com"),
                "000-000-0000",
                "000-00-0000",
                false,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                new LoyaltyPoints(10)
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

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        Customer customer = new Customer(
                new CustomerId(),
                new FullName("John", "Doe"),
                LocalDate.of(1990, 9, 18),
                "john.doe@email.com",
                "123-456-7890",
                "123-45-6789",
                false,
                OffsetDateTime.now()
        );

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        LoyaltyPoints expectedResult = new LoyaltyPoints(30);

        Assertions.assertThat(expectedResult).isEqualTo(customer.loyaltyPoints());
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = new Customer(
                new CustomerId(),
                new FullName("John", "Doe"),
                LocalDate.of(1990, 9, 18),
                "john.doe@email.com",
                "123-456-7890",
                "123-45-6789",
                false,
                OffsetDateTime.now()
        );

        LoyaltyPoints loyaltyPointsZero = new LoyaltyPoints(0);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(loyaltyPointsZero));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
    }
}