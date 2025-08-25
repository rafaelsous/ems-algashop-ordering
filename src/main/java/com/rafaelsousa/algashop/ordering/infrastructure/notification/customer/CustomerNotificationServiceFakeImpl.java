package com.rafaelsousa.algashop.ordering.infrastructure.notification.customer;

import com.rafaelsousa.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationApplicationService {
    private final Customers customers;

    @Override
    public void notifyNewRegistration(UUID rawCustomerId) {
        CustomerId customerId = new CustomerId(rawCustomerId);

        Customer customer = customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        log.info("Welcome {}", customer.fullName().firstName());
        log.info("Access your account using your email {}", customer.email().value());
    }
}