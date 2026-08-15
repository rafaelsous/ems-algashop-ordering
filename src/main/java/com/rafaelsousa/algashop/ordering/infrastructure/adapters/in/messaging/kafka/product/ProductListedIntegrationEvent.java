package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.messaging.kafka.product;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class ProductListedIntegrationEvent {
    private UUID productId;
    private OffsetDateTime listedAt;
}