package com.rafaelsousa.algashop.ordering.core.ports.out.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalOverviewOutput {
    private Integer activeCustomers;
    private Integer activeShoppingCarts;
}
