package com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ShoppingCartItemAddedEvent(ShoppingCartId shoppingCartId, CustomerId customerId,
                                         ProductId productId, OffsetDateTime addedAt) {
}