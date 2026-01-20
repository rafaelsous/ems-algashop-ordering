package com.rafaelsousa.algashop.ordering.core.application.shoppingcart.management;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartInput {

    @NotNull
    private UUID customerId;
}