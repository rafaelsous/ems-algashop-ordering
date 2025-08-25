package com.rafaelsousa.algashop.ordering.infrastructure.listener.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShoppingCartEventListener {

    @EventListener
    public void listen(ShoppingCartCreatedEvent shoppingCartCreatedEvent) {
        log.info("ShoppingCartCreatedEvent listen");
    }

    @EventListener
    public void listen(ShoppingCartEmptiedEvent shoppingCartEmptiedEvent) {
        log.info("ShoppingCartEmptiedEvent listen");
    }

    @EventListener
    public void listen(ShoppingCartItemAddedEvent shoppingCartItemAddedEvent) {
        log.info("ShoppingCartItemAddedEvent listen");
    }

    @EventListener
    public void listen(ShoppingCartItemRemovedEvent shoppingCartItemRemovedEvent) {
        log.info("ShoppingCartItemRemovedEvent listen");
    }
}