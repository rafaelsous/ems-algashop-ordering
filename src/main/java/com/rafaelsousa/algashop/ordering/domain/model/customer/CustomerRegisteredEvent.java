package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Email;
import com.rafaelsousa.algashop.ordering.domain.model.commons.FullName;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CustomerRegisteredEvent(CustomerId customerId, OffsetDateTime registeredAt, FullName fullName, Email email) {
}