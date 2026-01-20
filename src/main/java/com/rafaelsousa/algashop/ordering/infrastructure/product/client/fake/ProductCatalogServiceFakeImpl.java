package com.rafaelsousa.algashop.ordering.infrastructure.product.client.fake;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductName;

import java.util.Optional;

//@Component
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