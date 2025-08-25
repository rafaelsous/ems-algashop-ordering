package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record OrderReadyEvent(OrderId orderId, CustomerId customerId, OffsetDateTime readyAt) {
}