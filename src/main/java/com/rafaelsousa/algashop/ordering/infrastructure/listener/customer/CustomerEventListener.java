package com.rafaelsousa.algashop.ordering.infrastructure.listener.customer;

import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerEventListener {

    @EventListener
    public void listen(CustomerRegisteredEvent customerRegisteredEvent) {
        log.info("CustomerRegisteredEvent listen");
    }

    @EventListener
    public void listen(CustomerArchivedEvent customerArchivedEvent) {
        log.info("CustomerArchivedEvent listen");
    }
}