package com.rafaelsousa.algashop.ordering.domain.model.entity;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerTestDataBuilder {
    public static final CustomerId DEFAULT_CUSTOMER_ID = new CustomerId();

    private CustomerTestDataBuilder() {
    }

    public static Customer.BrandNewCustomerBuild brandNewCustomer() {
        return Customer.brandNew()
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
                                .build());
    }

    public static Customer.ExistingCustomerBuild existingAnonymizedCustomer() {
        return Customer.existing()
                .id(DEFAULT_CUSTOMER_ID)
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
                        .build());
    }

    public static Customer.ExistingCustomerBuild existingCustomer() {
        return Customer.existing()
                .id(DEFAULT_CUSTOMER_ID)
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 9, 18)))
                .email(new Email("john.doe@value.com"))
                .phone(new Phone("123-456-7890"))
                .document(new Document("123-45-6789"))
                .promotionNotificationsAllowed(false)
                .archived(false)
                .registeredAt(OffsetDateTime.now().minusDays(1))
                .archivedAt(null)
                .loyaltyPoints(LoyaltyPoints.ZERO)
                .address(
                        Address.builder()
                                .street("Bourbon Street")
                                .complement("Apt. 114")
                                .neighborhood("North Ville")
                                .number("1133")
                                .city("York")
                                .state("South California")
                                .zipCode(new ZipCode("12345"))
                                .build());
    }
}