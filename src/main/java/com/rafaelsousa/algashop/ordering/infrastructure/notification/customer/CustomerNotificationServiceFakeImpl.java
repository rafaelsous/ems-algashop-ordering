package com.rafaelsousa.algashop.ordering.infrastructure.notification.customer;

import com.rafaelsousa.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationApplicationService {

    @Override
    public void notifyNewRegistration(NotifyNewRegistrationInput notifyNewRegistrationInput) {
        log.info("Welcome {}", notifyNewRegistrationInput.firstName());
        log.info("Access your account using your email {}", notifyNewRegistrationInput.email());
    }
}