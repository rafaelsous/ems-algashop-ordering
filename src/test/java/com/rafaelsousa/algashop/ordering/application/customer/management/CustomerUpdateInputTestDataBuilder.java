package com.rafaelsousa.algashop.ordering.application.customer.management;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;

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