package com.rafaelsousa.algashop.ordering.core.domain.model.customer;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CustomerArchivedEvent(CustomerId customerId, OffsetDateTime archivedAt) {
}