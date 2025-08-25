package com.rafaelsousa.algashop.ordering.infrastructure.listener.customer;

import com.rafaelsousa.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {
    private final CustomerNotificationApplicationService customerNotificationApplicationService;

    @EventListener
    public void listen(CustomerRegisteredEvent customerRegisteredEvent) {
        log.info("CustomerRegisteredEvent listen");
        customerNotificationApplicationService.notifyNewRegistration(customerRegisteredEvent.customerId().value());
    }

    @EventListener
    public void listen(CustomerArchivedEvent customerArchivedEvent) {
        log.info("CustomerArchivedEvent listen");
    }
}