package com.rafaelsousa.algashop.ordering.infrastructure.product.client.http;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductName;
import com.rafaelsousa.algashop.ordering.presentation.BadGatewayException;
import com.rafaelsousa.algashop.ordering.presentation.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogServiceHttpImpl implements ProductCatalogService {
    private final ProductCatalogApiClient productCatalogApiClient;


    @Override
    public Optional<Product> ofId(ProductId productId) {
        ProductResponse productResponse;

        try {
            productResponse = productCatalogApiClient.getById(productId.value());
        } catch (ResourceAccessException ex) {
            throw new GatewayTimeoutException("Product Catalog API Timeout", ex);
        } catch (HttpClientErrorException ex) {
            throw new BadGatewayException("Product Catalog API Bad Gateway");
        }

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
