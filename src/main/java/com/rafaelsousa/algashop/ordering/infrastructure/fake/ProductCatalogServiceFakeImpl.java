package com.rafaelsousa.algashop.ordering.infrastructure.fake;

import com.rafaelsousa.algashop.ordering.domain.model.service.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;

import java.util.Optional;

public class ProductCatalogServiceFakeImpl implements ProductCatalogService {

    @Override
    public Optional<Product> ofId(ProductId productId) {
        Product product = Product.builder()
                .id(productId)
                .name(ProductName.of("Notebook"))
                .price(Money.of("4700.00"))
                .inStock(true)
                .build();

        return Optional.of(product);
    }
}