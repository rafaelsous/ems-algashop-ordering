package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.notification.customer;

import com.rafaelsousa.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ForNotifyingCustomersFakeImpl implements ForNotifyingCustomers {

    @Override
    public void notifyNewRegistration(NotifyNewRegistrationInput notifyNewRegistrationInput) {
        log.info("Welcome {}", notifyNewRegistrationInput.firstName());
        log.info("Access your account using your email {}", notifyNewRegistrationInput.email());
    }
}