package com.rafaelsousa.algashop.ordering.application.service;

import com.rafaelsousa.algashop.ordering.application.model.AddressData;
import com.rafaelsousa.algashop.ordering.application.model.CustomerInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerManagementApplicationServiceIT {

    private final CustomerManagementApplicationService customerManagementApplicationService;

    @Autowired
    CustomerManagementApplicationServiceIT(CustomerManagementApplicationService customerManagementApplicationService) {
        this.customerManagementApplicationService = customerManagementApplicationService;
    }

    @Test
    void shouldRegister() {
        CustomerInput customerInput = CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .bithDate(LocalDate.of(1970, 3, 21))
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
                        .build())
                .build();

        UUID customerId = customerManagementApplicationService.create(customerInput);

        assertThat(customerId).isNotNull();
    }
}