package com.rafaelsousa.algashop.ordering.core.application.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ShippingCostPreviewOutput {
	private BigDecimal cost;
	private LocalDate expectedDate;
}