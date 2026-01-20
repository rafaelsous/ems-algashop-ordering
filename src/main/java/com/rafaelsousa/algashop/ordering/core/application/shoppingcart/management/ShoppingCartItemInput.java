package com.rafaelsousa.algashop.ordering.core.application.shoppingcart.management;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private Integer quantity;
}