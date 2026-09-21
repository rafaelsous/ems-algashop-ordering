package com.rafaelsousa.algashop.ordering.core.application.product.event;


import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;

import com.rafaelsousa.algashop.ordering.core.domain.model.IdGenerator;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceChangedIntegrationEvent implements IntegrationEvent {
	private UUID productId;
	private OffsetDateTime changedAt;
	private UUID idempotencyKey = IdGenerator.generateTimeBasedUUID();

	@Override
	public String getAggregateId() {
		if (productId == null) {
			return null;
		}

		return productId.toString();
	}
}
