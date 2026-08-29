package com.rafaelsousa.algashop.ordering.core.application.product.event;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceChangedV2IntegrationEvent implements IntegrationEvent {
	private UUID productId;
	private OffsetDateTime changedAt;
	private BigDecimal oldRegularPrice;
	private BigDecimal oldSalePrice;
	private BigDecimal newRegularPrice;
	private BigDecimal newSalePrice;

	@Override
	public String getAggregateId() {
		if (productId == null) {
			return null;
		}

		return productId.toString();
	}
}
