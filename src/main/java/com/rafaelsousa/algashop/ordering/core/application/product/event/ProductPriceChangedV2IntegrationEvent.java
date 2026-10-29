package com.rafaelsousa.algashop.ordering.core.application.product.event;

import com.rafaelsousa.algashop.ordering.core.application.InboundIntegrationEvent;

import jakarta.validation.constraints.NotNull;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceChangedV2IntegrationEvent implements InboundIntegrationEvent {

	@NotNull
	private UUID productId;

	@NotNull
	private OffsetDateTime changedAt;

	@NotNull
	private BigDecimal oldRegularPrice;

	@NotNull
	private BigDecimal oldSalePrice;

	@NotNull
	private BigDecimal newRegularPrice;

	@NotNull
	private BigDecimal newSalePrice;
}
