package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ShoppingCartEmptiedEvent(ShoppingCartId shoppingCartId, CustomerId customerId, OffsetDateTime emptiedAt) {
}