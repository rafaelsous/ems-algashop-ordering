package com.rafaelsousa.algashop.ordering;


import com.rafaelsousa.algashop.ordering.domain.entity.Customer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

class CustomerTest {

    @Test
    void testingCustomer() {
        Customer customer = new Customer(
                UUID.randomUUID(),
                "John Doe",
                LocalDate.of(1990, 6, 14),
                "john.doe@email.com",
                "258-369-7894",
                "123.456",
                true,
                OffsetDateTime.now()
        );

        customer.addLoyaltyPoints(10);
    }
}