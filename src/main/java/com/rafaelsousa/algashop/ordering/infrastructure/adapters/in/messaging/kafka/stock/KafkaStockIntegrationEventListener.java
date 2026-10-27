package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.messaging.kafka.stock;

import com.rafaelsousa.algashop.ordering.core.application.stock.event.StockReservationConfirmedIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.ForManagingOrders;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        id = "#{algaShopMessagingKafkaProperties.stockEventsConsumerGroup}",
        concurrency = "3",
        topics = "#{algaShopMessagingKafkaProperties.stockEventTopicName}")
public class KafkaStockIntegrationEventListener {
    private final ForManagingOrders forManagingOrders;

    @KafkaHandler(isDefault = true)
    public void handle(
            @Payload Object event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        log.info(
                "Event ignored: type={} key={} offset={}",
                event.getClass().getSimpleName(),
                messageKey,
                offset);
    }

    @KafkaHandler
    public void handle(
            @Payload @Valid StockReservationConfirmedIntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        logReceived(event, messageKey, partition, offset);

        forManagingOrders.markAsReady(event.getOrderId());
    }

    private void logReceived(Object event, String messageKey, Integer partition, Long offset) {
        log.info(
                "Received {} | key={} | partiton={} | offset={} | thread={}",
                event.getClass().getSimpleName(),
                messageKey,
                partition,
                offset,
                Thread.currentThread().getName());
    }
}
