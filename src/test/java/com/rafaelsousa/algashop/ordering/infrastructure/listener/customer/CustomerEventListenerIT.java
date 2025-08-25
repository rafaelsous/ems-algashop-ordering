package com.rafaelsousa.algashop.ordering.infrastructure.listener.customer;

import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerLoyaltyPointsApplicationService;
import com.rafaelsousa.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.rafaelsousa.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Email;
import com.rafaelsousa.algashop.ordering.domain.model.commons.FullName;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderReadyEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CustomerEventListenerIT {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    CustomerEventListenerIT(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoBean
    private CustomerNotificationApplicationService customerNotificationApplicationService;

    @MockitoBean
    private CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @Test
    void shouldListenCustomerRegisteredEvent() {
        applicationEventPublisher.publishEvent(CustomerRegisteredEvent.builder()
                .customerId(new CustomerId())
                .fullName(FullName.of("John", "Doe"))
                .email(Email.of("john.doe@example.com"))
                .build());

        verify(customerEventListener).listen(any(CustomerRegisteredEvent.class));
        verify(customerNotificationApplicationService).notifyNewRegistration(any(NotifyNewRegistrationInput.class));
    }

    @Test
    void shouldListemCustomerArchivedEvent() {
        applicationEventPublisher.publishEvent(CustomerArchivedEvent.builder()
                .customerId(new CustomerId())
                .archivedAt(OffsetDateTime.now())
                .build());

        verify(customerEventListener).listen(any(CustomerArchivedEvent.class));
    }

    @Test
    void shouldListenOrderReadyEvent() {
        applicationEventPublisher.publishEvent(OrderReadyEvent.builder()
                .orderId(new OrderId())
                .customerId(new CustomerId())
                .readyAt(OffsetDateTime.now())
                .build());

        verify(customerEventListener).listen(any(OrderReadyEvent.class));
        verify(customerLoyaltyPointsApplicationService).addLoyaltyPoints(any(UUID.class), any(String.class));
    }
}