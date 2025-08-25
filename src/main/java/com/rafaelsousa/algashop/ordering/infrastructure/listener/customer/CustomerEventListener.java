package com.rafaelsousa.algashop.ordering.infrastructure.listener.customer;

import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerLoyaltyPointsApplicationService;
import com.rafaelsousa.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.rafaelsousa.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {
    private final CustomerNotificationApplicationService customerNotificationApplicationService;
    private final CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @EventListener
    public void listen(CustomerRegisteredEvent customerRegisteredEvent) {
        log.info("CustomerRegisteredEvent listen");
        customerNotificationApplicationService.notifyNewRegistration(NotifyNewRegistrationInput.builder()
                .rawCustomerId(customerRegisteredEvent.customerId().value())
                .firstName(customerRegisteredEvent.fullName().firstName())
                .email(customerRegisteredEvent.email().value())
                .build());
    }

    @EventListener
    public void listen(CustomerArchivedEvent customerArchivedEvent) {
        log.info("CustomerArchivedEvent listen");
    }

    @EventListener
    public void listen(OrderReadyEvent orderReadyEvent) {
        customerLoyaltyPointsApplicationService.addLoyaltyPoints(orderReadyEvent.customerId().value(),
                orderReadyEvent.orderId().toString());
    }
}