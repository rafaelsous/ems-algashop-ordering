package com.rafaelsousa.algashop.ordering.application.customer.management;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;
import com.rafaelsousa.algashop.ordering.domain.model.customer.LoyaltyPoints;
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
    void shouldRegisterAndFindCustomer() {
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

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        assertThat(customerOutput).satisfies(
                co -> assertThat(co.getId()).isEqualTo(customerId),
                co -> assertThat(co.getFirstName()).isEqualTo(customerInput.getFirstName()),
                co -> assertThat(co.getLastName()).isEqualTo(customerInput.getLastName()),
                co -> assertThat(co.getBirthDate()).isEqualTo(customerOutput.getBirthDate()),
                co -> assertThat(co.getDocument()).isEqualTo(customerInput.getDocument()),
                co -> assertThat(co.getPhone()).isEqualTo(customerInput.getPhone()),
                co -> assertThat(co.getEmail()).isEqualTo(customerInput.getEmail()),
                co -> assertThat(co.getPromotionNotificationsAllowed()).isFalse(),
                co -> assertThat(co.getArchived()).isFalse(),
                co -> assertThat(co.getRegisteredAt()).isNotNull(),
                co -> assertThat(co.getLoyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO.value()),
                co -> {
                    AddressData address = customerInput.getAddress();
                    assertThat(co.getAddress()).satisfies(
                            ad -> assertThat(ad.getStreet()).isEqualTo(address.getStreet()),
                            ad -> assertThat(ad.getNumber()).isEqualTo(address.getNumber()),
                            ad -> assertThat(ad.getComplement()).isEqualTo(address.getComplement()),
                            ad -> assertThat(ad.getNeighborhood()).isEqualTo(address.getNeighborhood()),
                            ad -> assertThat(ad.getCity()).isEqualTo(address.getCity()),
                            ad -> assertThat(ad.getState()).isEqualTo(address.getState()),
                            ad -> assertThat(ad.getZipCode()).isEqualTo(address.getZipCode())
                    );
                }
        );
    }
}