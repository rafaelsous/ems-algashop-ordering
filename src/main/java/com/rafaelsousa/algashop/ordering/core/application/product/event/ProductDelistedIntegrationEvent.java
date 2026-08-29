package com.rafaelsousa.algashop.ordering.core.application.product.event;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDelistedIntegrationEvent implements IntegrationEvent {
    private UUID productId;
    private OffsetDateTime delistedAt;

    @Override
    public String getAggregateId() {
        if (productId == null) {
            return null;
        }

        return productId.toString();
    }
}