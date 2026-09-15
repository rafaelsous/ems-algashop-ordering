package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.messaging.kafka.product;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.product.event.ProductDelistedIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.product.event.ProductListedIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.product.event.ProductPriceChangedV2IntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ForManagingShoppingCarts;

import com.rafaelsousa.algashop.ordering.infrastructure.config.cache.ProductCacheManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
    id = "ordering.product-events",
    idIsGroup = false,
    concurrency = "3",
    topics = "#{algaShopMessagingKafkaProperties.productEventTopicName}"
)
public class KafkaProductIntegrationEventListener {
    private final ProductCacheManager productCacheManager;
    private final ForManagingShoppingCarts forManagingShoppingCarts;

    @Value("${simulate:none}") // none | slow | technical | business
    private String simulate;

    @KafkaHandler(isDefault = true)
    public void handle(
            Object event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.OFFSET, required = false) String messageOffset) {
        log.info("Event ignored {}: key: {} and offset: {}", event, messageKey, messageOffset);
    }

    @KafkaHandler
    public void handle(
            ProductListedIntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
        log(event, messageKey, partition, offset);

//        simulateProcessing();

        productCacheManager.evict(event.getProductId());
        forManagingShoppingCarts.changeProductAvailability(event.getProductId(), true);
    }

    @KafkaHandler
    public void handle(
            ProductDelistedIntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
        log(event, messageKey, partition, offset);

//        simulateProcessing();

        productCacheManager.evict(event.getProductId());
        forManagingShoppingCarts.changeProductAvailability(event.getProductId(), false);
    }

    @KafkaHandler
    public void handle(
            @Valid ProductPriceChangedV2IntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
        log(event, messageKey, partition, offset);

        productCacheManager.evict(event.getProductId());
        forManagingShoppingCarts.refreshProductPrice(event.getProductId(), event.getNewSalePrice());

        log.warn("Mail send product price on shopping cart was been updated.");

        simulateProcessing();
    }

    private void simulateProcessing() {
        switch (simulate) {
            case  "slow" -> {
                log.warn("Simulating slow processing...");
                try {
                    Thread.sleep(Duration.ofSeconds(30));
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                }
            }
            case "technical" -> throw new RuntimeException("Simulated technical exception");
            case "business" -> throw new DomainException("Simulated business exception");
            default -> log.info("No simulation");
        }
    }

    private static void log(IntegrationEvent event, String messageKey, Integer partition, Integer offset) {
        log.info("Received event: {} with key: {}, partition: {}, offset: {}, and thread: {}",
            event.getClass().getSimpleName(), messageKey, partition, offset, Thread.currentThread().getName());
    }
}
