package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ShoppingCartCreatedEvent(ShoppingCartId shoppingCartId, CustomerId customerId, OffsetDateTime createdAt) {
}