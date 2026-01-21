package com.rafaelsousa.algashop.ordering.core.application.customer.management;

import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerInput;

import java.time.LocalDate;

public class CustomerInputTestDataBuilder {

    public static CustomerInput.CustomerInputBuilder aCustomer() {
        return CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1970, 3, 21))
                .document("123-45-6789")
                .phone("123-456-7980")
                .email("john.doe@email.com")
                .promotionNotificationsAllowed(false)
                .address(AddressData.builder()
                        .street("Bourbon Street")
                        .number("1207")
                        .complement("Apt. 1001")
                        .neighborhood("North Ville")
                        .city("Yostfort")
                        .state("South Carolina")
                        .zipCode("12345")
                        .build());
    }
}