package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.UUID;

public interface ProductCatalogApiClient {

    @GetExchange(value = "/api/v1/products/{productId}", accept = MediaType.APPLICATION_JSON_VALUE)
    ProductResponse getById(@PathVariable UUID productId);
}
