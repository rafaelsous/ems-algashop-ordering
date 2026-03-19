package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductName;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCatalogServiceHttpImpl implements ProductCatalogService {
    private final ResilientProductCatalogApiClient productCatalogApiClient;

    @Override
    public Optional<Product> ofId(ProductId productId) {
        return productCatalogApiClient.getById(productId.value())
	        .map(productResponse -> 
                Product.builder()
                        .id(new ProductId(productResponse.getId()))
                        .name(ProductName.of(productResponse.getName()))
                        .price(Money.of(productResponse.getSalePrice()))
                        .inStock(productResponse.isInStock())
                        .build()
        );
    }
}