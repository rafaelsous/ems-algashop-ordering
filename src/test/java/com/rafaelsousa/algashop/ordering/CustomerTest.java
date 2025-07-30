package com.rafaelsousa.algashop.ordering;

import com.rafaelsousa.algashop.ordering.domain.entity.Customer;
import com.rafaelsousa.algashop.ordering.domain.utils.IdGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CustomerTest {
    private static final Logger log = LoggerFactory.getLogger(CustomerTest.class);

    @Test
    void testingCustomer() {
        Customer customer = new Customer(
                IdGenerator.generateTimeBasedUUID(),
                "John Doe",
                LocalDate.of(1990, 6, 14),
                "john.doe@email.com",
                "258-369-7894",
                "123.456",
                true,
                OffsetDateTime.now()
        );

        log.info("{}", customer.id());
        log.info("{}", IdGenerator.generateTimeBasedUUID());

        customer.addLoyaltyPoints(10);
    }
}