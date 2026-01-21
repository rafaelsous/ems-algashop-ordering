package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.listener.customer;

import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerArchivedEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {
    private final ForConfirmCustomerRegistration forConfirmCustomerRegistration;
    private final ForAddingLoyaltyPoints forAddingLoyaltyPoints;

    @EventListener
    public void listen(CustomerRegisteredEvent customerRegisteredEvent) {
        log.info("CustomerRegisteredEvent listen");
        forConfirmCustomerRegistration.confirm(customerRegisteredEvent.customerId().value());
    }

    @EventListener
    public void listen(CustomerArchivedEvent customerArchivedEvent) {
        log.info("CustomerArchivedEvent listen");
    }

    @EventListener
    public void listen(OrderReadyEvent orderReadyEvent) {
        forAddingLoyaltyPoints.addLoyaltyPoints(orderReadyEvent.customerId().value(),
                orderReadyEvent.orderId().toString());
    }
}