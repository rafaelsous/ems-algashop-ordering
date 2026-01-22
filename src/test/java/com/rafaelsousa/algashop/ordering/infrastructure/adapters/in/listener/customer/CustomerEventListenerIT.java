package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.listener.customer;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Email;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.FullName;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerArchivedEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import com.rafaelsousa.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.AbstractInfrastructureIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class CustomerEventListenerIT extends AbstractInfrastructureIT {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    CustomerEventListenerIT(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoBean
    private ForNotifyingCustomers forNotifyingCustomers;

    @MockitoBean
    private ForAddingLoyaltyPoints forAddingLoyaltyPoints;

    @MockitoBean
    private ForConfirmCustomerRegistration forConfirmCustomerRegistration;

    @Test
    void shouldListenCustomerRegisteredEvent() {
        applicationEventPublisher.publishEvent(CustomerRegisteredEvent.builder()
                .customerId(new CustomerId())
                .fullName(FullName.of("John", "Doe"))
                .email(Email.of("john.doe@example.com"))
                .build());

        verify(customerEventListener).listen(any(CustomerRegisteredEvent.class));
        verify(forConfirmCustomerRegistration).confirm(any(UUID.class));
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
        verify(forAddingLoyaltyPoints).addLoyaltyPoints(any(UUID.class), any(String.class));
    }
}