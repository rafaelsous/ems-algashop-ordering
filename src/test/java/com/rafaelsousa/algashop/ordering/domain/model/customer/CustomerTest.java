package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.commons.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    @SuppressWarnings("java:S5778")
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        CustomerTestDataBuilder.brandNewCustomer()
                                .email(new Email("invalid_email"))
                                .build()
                );
    }

    @Test
    @SuppressWarnings("java:S5778")
    void given_invalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.changeEmail(new Email("invalid_email")));
    }

    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customer.archive();

        FullName fullNameAnonymous = new FullName("Anonymous", "Anonymous");

        Assertions.assertWith(customer,
                c -> assertThat(c.fullName()).isEqualTo(fullNameAnonymous),
                c -> assertThat(c.birthDate()).isNull(),
                c -> assertThat(c.email()).isNotEqualTo(new Email("john.doe@value.com")),
                c -> assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
                c -> assertThat(c.document()).isEqualTo(new Document("000-00-0000")),
                c -> assertThat(c.archivedAt()).isNotNull(),
                c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
                c -> assertThat(c.address()).isEqualTo(Address.builder()
                        .street("Bourbon Street")
                        .complement(null)
                        .neighborhood("North Ville")
                        .number("Anonymized")
                        .city("York")
                        .state("South California")
                        .zipCode(new ZipCode("12345"))
                        .build())
        );
    }

    @Test
    void given_archivedCustomer_whenTryUpdate_shouldGenerateException() {
        Customer customer = CustomerTestDataBuilder.existingAnonymizedCustomer().build();

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);

        Email newEmail = new Email("example@value.com");
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeEmail(newEmail));

        Phone newPhone = new Phone("000-000-0000");
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changePhone(newPhone));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enablePromotionNotifications);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disablePromotionNotifications);
    }

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        LoyaltyPoints expectedResult = new LoyaltyPoints(30);

        Assertions.assertThat(expectedResult).isEqualTo(customer.loyaltyPoints());
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        LoyaltyPoints loyaltyPointsZero = new LoyaltyPoints(0);

        Assertions.assertThatNoException()
                .isThrownBy(() -> customer.addLoyaltyPoints(loyaltyPointsZero));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new LoyaltyPoints(-10));
    }

    @Test
    void givenValidData_whenCreateBrandNewCustomer_shouldGenerateCustomerRegisteredEvent() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        CustomerRegisteredEvent customerRegisteredEvent = CustomerRegisteredEvent.builder()
                .customerId(customer.id())
                .registeredAt(customer.registeredAt())
                .build();

        assertThat(customer.domainEvents()).contains(customerRegisteredEvent);
    }
}