package com.rafaelsousa.algashop.ordering.application.shoppingcart.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartOutput {
    private UUID id;
    private UUID customerId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private List<ShoppingCartItemOutput> items = new ArrayList<>();
}