package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.listener.order;

import com.rafaelsousa.algashop.ordering.core.application.notification.command.SendEmailIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.application.order.event.*;
import com.rafaelsousa.algashop.ordering.core.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.*;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.saga.ForCoordinatingPlaceOrderSaga;
import com.rafaelsousa.algashop.ordering.core.ports.notification.ForPublishingNotificationIntegrationCommands;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationEvents;

import com.rafaelsousa.algashop.ordering.infrastructure.config.email.NotificationsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final Orders orders;
    private final Mapper mapper;
    private final Customers customers;
    private final ForCoordinatingPlaceOrderSaga forCoordinatingPlaceOrderSaga;
    private final OrderPaidIntegrationEventAssembler orderPaidIntegrationEventAssembler;
    private final ForPublishingOrderIntegrationEvents forPublishingOrderIntegrationEvents;
    private final OrderPlacedIntegrationEventAssembler orderPlacedIntegrationEventAssembler;

    private final NotificationsProperties notificationsProperties;
    private final OrderPlacedEmailCommandAssembler orderPlacedEmailCommandAssembler;
    private final ForPublishingNotificationIntegrationCommands forPublishingNotificationIntegrationCommands;

    @EventListener
    public void onOrderPlacedPublishIntegrationEvent(OrderPlacedEvent event) {
        Order order = orders.ofId(event.orderId()).orElseThrow(OrderNotFoundException::new);
        OrderPlacedIntegrationEvent integrationEvent =
                orderPlacedIntegrationEventAssembler.toIntegrationEvent(order);

        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void onOrderPlacedStartSaga(OrderPlacedEvent event) {
        forCoordinatingPlaceOrderSaga.start(event.orderId().toString());
    }

    @EventListener
    public void onOrderPlacedSendNotificationEmail(OrderPlacedEvent event) {
        Order order = orders.ofId(event.orderId()).orElseThrow(OrderNotFoundException::new);
        Customer customer = customers.ofId(order.customerId())
            .orElseThrow(() -> new CustomerNotFoundException(order.customerId()));

        UUID templateId = notificationsProperties.getEmailTemplates().getOrderPlaced();
        SendEmailIntegrationCommand command = orderPlacedEmailCommandAssembler.toCommand(order, customer, templateId);

        forPublishingNotificationIntegrationCommands.send(command);
    }

    @EventListener
    public void listen(OrderPaidEvent event) {
        Order order = orders.ofId(event.orderId()).orElseThrow(OrderNotFoundException::new);
        OrderPaidIntegrationEvent integrationEvent =
                orderPaidIntegrationEventAssembler.toIntegrationEvent(order);

        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        OrderReadyIntegrationEvent integrationEvent =
                mapper.convert(event, OrderReadyIntegrationEvent.class);
        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void listen(OrderCanceledEvent event) {
        OrderCanceledIntegrationEvent integrationEvent =
                mapper.convert(event, OrderCanceledIntegrationEvent.class);
        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }
}
