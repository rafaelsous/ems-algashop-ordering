package com.rafaelsousa.algashop.ordering.core.application.product.event;

import com.rafaelsousa.algashop.ordering.core.application.InboundIntegrationEvent;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSoldOutIntegrationEvent implements InboundIntegrationEvent {
    private UUID productId;
    private OffsetDateTime soldOutAt;
}
