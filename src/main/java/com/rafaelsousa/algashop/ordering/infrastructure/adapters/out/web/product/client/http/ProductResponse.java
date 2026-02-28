package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements Serializable {
    private UUID id;
    private String name;
    private BigDecimal salePrice;
    private boolean inStock;
}