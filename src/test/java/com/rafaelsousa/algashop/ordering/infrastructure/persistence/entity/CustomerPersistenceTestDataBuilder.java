package com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Address;
import com.rafaelsousa.algashop.ordering.domain.model.commons.ZipCode;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddableAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistence.CustomerPersistenceBuilder;

import java.time.LocalDate;

import static com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class CustomerPersistenceTestDataBuilder {

    public static CustomerPersistenceBuilder aCustomer() {
        return CustomerPersistence.builder()
                .id(DEFAULT_CUSTOMER_ID.value())
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 9, 18))
                .email("john.doe@value.com")
                .phone("123-456-7890")
                .document("123-45-6789")
                .promotionNotificationsAllowed(false)
                .address(
                        AddressEmbeddableAssembler.fromPersistence(
                                Address.builder()
                                        .street("Bourbon Street")
                                        .complement("Apt. 114")
                                        .neighborhood("North Ville")
                                        .number("1133")
                                        .city("York")
                                        .state("South California")
                                        .zipCode(new ZipCode("12345"))
                                        .build()));
    }
}