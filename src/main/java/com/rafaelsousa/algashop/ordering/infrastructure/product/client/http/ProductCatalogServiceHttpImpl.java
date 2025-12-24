package com.rafaelsousa.algashop.ordering.infrastructure.product.client.http;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogServiceHttpImpl implements ProductCatalogService {
    private final ProductCatalogApiClient productCatalogApiClient;


    @Override
    public Optional<Product> ofId(ProductId productId) {
        ProductResponse productResponse = productCatalogApiClient.getById(productId.value());

        return Optional.of(
                Product.builder()
                        .id(new ProductId(productResponse.getId()))
                        .name(ProductName.of(productResponse.getName()))
                        .price(Money.of(productResponse.getSalePrice()))
                        .inStock(productResponse.isInStock())
                        .build()
        );
    }
}
