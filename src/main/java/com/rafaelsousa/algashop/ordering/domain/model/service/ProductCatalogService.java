package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;

import java.util.Optional;

public interface ProductCatalogService {
    Optional<Product> ofId(ProductId productId);
}