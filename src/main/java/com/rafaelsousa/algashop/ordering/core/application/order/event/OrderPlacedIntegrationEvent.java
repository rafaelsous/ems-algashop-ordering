package com.rafaelsousa.algashop.ordering.core.application.order.event;

import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationEvent;
import com.rafaelsousa.algashop.ordering.core.application.order.snapshot.BillingSnapshot;
import com.rafaelsousa.algashop.ordering.core.application.order.snapshot.OrderItemSnapshot;
import com.rafaelsousa.algashop.ordering.core.application.order.snapshot.PaymentSnapshot;
import com.rafaelsousa.algashop.ordering.core.application.order.snapshot.ShippingSnapshot;
import com.rafaelsousa.algashop.ordering.core.domain.model.IdGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedIntegrationEvent implements OutboundIntegrationEvent {

	@NotBlank
    private String orderId;

	@NotNull
	private UUID customerId;

	@NotNull
    private OffsetDateTime placedAt;

	@NotNull
	@Builder.Default
	private UUID idempotencyKey = IdGenerator.generateTimeBasedUUID();

	@NotNull @Positive
	private BigDecimal totalAmount;

	@Builder.Default
	@NotNull @Valid @Size(min = 1)
	private List<OrderItemSnapshot> items = new ArrayList<>();

	@NotNull @Valid
	private PaymentSnapshot payment;

	@NotNull @Valid
	private ShippingSnapshot shipping;

	@NotNull @Valid
	private BillingSnapshot billing;

	@Override
	public String getAggregateId() {
		return orderId;
	}
}
