package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.messaging.kafka.product;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.product.event.ProductDelistedIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.product.event.ProductListedIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.product.event.ProductPriceChangedIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.product.event.ProductPriceChangedV2IntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ForManagingShoppingCarts;

import com.rafaelsousa.algashop.ordering.infrastructure.config.cache.ProductCacheManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "#{algaShopMessagingKafkaProperties.productEventTopicName}")
public class KafkaProductIntegrationEventListener {
    private final ProductCacheManager productCacheManager;
    private final ForManagingShoppingCarts forManagingShoppingCarts;

    @KafkaHandler(isDefault = true)
    public void handle(
            Object event,
            @Header(value = KafkaHeaders.RECEIVED_KEY) String messageKey,
            @Header(KafkaHeaders.OFFSET) String messageOffset) {
        log.info("Event ignored {}: key: {} and offset: {}", event, messageKey, messageOffset);
    }

    @KafkaHandler
    public void handle(
            ProductListedIntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log(event, messageKey);
        productCacheManager.evict(event.getProductId());
        forManagingShoppingCarts.changeProductAvailability(event.getProductId(), true);
    }

    @KafkaHandler
    public void handle(
            ProductDelistedIntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log(event, messageKey);
        productCacheManager.evict(event.getProductId());
        forManagingShoppingCarts.changeProductAvailability(event.getProductId(), false);
    }

    @KafkaHandler
    public void handle(
            @Valid ProductPriceChangedV2IntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log(event, messageKey);
        productCacheManager.evict(event.getProductId());
        forManagingShoppingCarts.refreshProductPrice(event.getProductId(), event.getNewSalePrice());
    }

    private static void log(IntegrationEvent event, String messageKey) {
        log.info("Received {} with key: {} and payload: {}", event.getClass(), messageKey, event);
    }
}
