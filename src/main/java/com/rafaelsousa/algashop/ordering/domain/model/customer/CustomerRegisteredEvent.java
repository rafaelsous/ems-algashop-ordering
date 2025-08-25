package com.rafaelsousa.algashop.ordering.domain.model.customer;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CustomerRegisteredEvent(CustomerId customerId, OffsetDateTime registeredAt) {
}