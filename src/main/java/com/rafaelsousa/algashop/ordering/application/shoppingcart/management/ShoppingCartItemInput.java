package com.rafaelsousa.algashop.ordering.application.shoppingcart.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemInput {
    private UUID shoppingCartId;
    private UUID productId;
    private Integer quantity;
}