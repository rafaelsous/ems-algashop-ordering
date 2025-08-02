package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.exception.CustomerArchivedException;
import com.rafaelsousa.algashop.ordering.domain.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        FullName fullName = new FullName("John", "Doe");
        BirthDate birthDate = new BirthDate(LocalDate.of(1990, 9, 18));
        Phone phone = new Phone("123-456-7890");
        Document document = new Document("123-45-6789");
        Address address = Address.builder()
                .street("Bourbon Street")
                .complement("Apt. 114")
                .neighborhood("North Ville")
                .number("1133")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        Customer.brandNew()
                                .fullName(fullName)
                                .birthDate(birthDate)
                                .email(new Email("invalid_email"))
                                .phone(phone)
                                .document(document)
                                .promotionNotificationsAllowed(false)
                                .address(address)
                                .build()
                );
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        Customer customer = Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 9, 18)))
                .email(new Email("john.doe@value.com"))
                .phone(new Phone("123-456-7890"))
                .document(new Document("123-45-6789"))
                .promotionNotificationsAllowed(false)
                .address(
                        Address.builder()
                                .street("Bourbon Street")
                                .complement("Apt. 114")
                                .neighborhood("North Ville")
                                .number("1133")
                                .city("York")
                                .state("South California")
                                .zipCode(new ZipCode("12345"))
                                .build())
                .build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.changeEmail(new Email("invalid_email")));
    }

    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 9, 18)))
                .email(new Email("john.doe@value.com"))
                .phone(new Phone("123-456-7890"))
                .document(new Document("123-45-6789"))
                .promotionNotificationsAllowed(false)
                .address(
                        Address.builder()
                                .street("Bourbon Street")
                                .complement("Apt. 114")
                                .neighborhood("North Ville")
                                .number("1133")
                                .city("York")
                                .state("South California")
                                .zipCode(new ZipCode("12345"))
                                .build())
                .build();

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
        Customer customer = Customer.existing()
                .id(new CustomerId())
                .fullName(new FullName("Anonymous", "Anonymous"))
                .birthDate(null)
                .email(new Email(UUID.randomUUID().toString().concat("@anonymous.com")))
                .phone(new Phone("000-000-0000"))
                .document(new Document("000-00-0000"))
                .promotionNotificationsAllowed(false)
                .archived(true)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now())
                .loyaltyPoints(new LoyaltyPoints(10))
                .address(Address.builder()
                        .street("Bourbon Street")
                        .complement("Apt. 114")
                        .neighborhood("North Ville")
                        .number("1133")
                        .city("York")
                        .state("South California")
                        .zipCode(new ZipCode("12345"))
                        .build())
                .build();

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);

        Email newEmail = new Email("example@value.com");
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changeEmail(newEmail);
                });

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
        Customer customer = Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 9, 18)))
                .email(new Email("john.doe@value.com"))
                .phone(new Phone("123-456-7890"))
                .document(new Document("123-45-6789"))
                .promotionNotificationsAllowed(false)
                .address(
                        Address.builder()
                                .street("Bourbon Street")
                                .complement("Apt. 114")
                                .neighborhood("North Ville")
                                .number("1133")
                                .city("York")
                                .state("South California")
                                .zipCode(new ZipCode("12345"))
                                .build())
                .build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        LoyaltyPoints expectedResult = new LoyaltyPoints(30);

        Assertions.assertThat(expectedResult).isEqualTo(customer.loyaltyPoints());
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 9, 18)))
                .email(new Email("john.doe@value.com"))
                .phone(new Phone("123-456-7890"))
                .document(new Document("123-45-6789"))
                .promotionNotificationsAllowed(false)
                .address(
                        Address.builder()
                                .street("Bourbon Street")
                                .complement("Apt. 114")
                                .neighborhood("North Ville")
                                .number("1133")
                                .city("York")
                                .state("South California")
                                .zipCode(new ZipCode("12345"))
                                .build())
                .build();

        LoyaltyPoints loyaltyPointsZero = new LoyaltyPoints(0);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(loyaltyPointsZero));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
    }
}