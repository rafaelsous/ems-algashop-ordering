package com.rafaelsousa.algashop.ordering.core.application.customer.management;

import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerUpdateInput;

public class CustomerUpdateInputTestDataBuilder {

    public static CustomerUpdateInput.CustomerUpdateInputBuilder aCustomerUpdate() {
        return CustomerUpdateInput.builder()
                .firstName("Leonardo")
                .lastName("DiCaprio")
                .phone("012-987-1256")
                .promotionNotificationsAllowed(true)
                .address(AddressData.builder()
                        .street("Schowalter Lakes")
                        .number("65726")
                        .complement("Apt. 843")
                        .neighborhood("Norrisville")
                        .city("Yostfort")
                        .state("California")
                        .zipCode("33837")
                        .build());
    }
}