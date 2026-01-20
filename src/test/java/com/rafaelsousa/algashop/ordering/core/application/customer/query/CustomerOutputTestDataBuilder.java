package com.rafaelsousa.algashop.ordering.core.application.customer.query;

import com.rafaelsousa.algashop.ordering.core.application.commons.AddressData;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerOutputTestDataBuilder {

    public static CustomerOutput.CustomerOutputBuilder existing() {
        return CustomerOutput.builder()
                .id(UUID.randomUUID())
                .registeredAt(OffsetDateTime.now())
                .phone("1191234564")
                .email("johndoe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .document("12345")
                .promotionNotificationsAllowed(false)
                .loyaltyPoints(0)
                .archived(false)
                .address(AddressData.builder()
                        .street("123 Main St")
                        .number("100")
                        .complement("Apt 4B")
                        .neighborhood("Downtown")
                        .city("Springfield")
                        .state("South Carolina")
                        .zipCode("62701")
                        .build());
    }
}