package com.rafaelsousa.algashop.ordering.core.application.order.snapshot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemSnapshot(
        @NotBlank String id,
		@NotBlank String orderItemId,
        @NotNull UUID productId,
        @NotBlank String productName,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotNull @PositiveOrZero Integer quantity,
        @NotNull @PositiveOrZero BigDecimal totalAmount) {}
