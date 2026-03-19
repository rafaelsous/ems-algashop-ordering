package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductName;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCatalogServiceHttpImpl implements ProductCatalogService {
    private final ProductCatalogApiClient productCatalogApiClient;


    @Override
    @Retryable(maxRetries = 3, delayString = "3s", multiplier = 2, includes = {GatewayTimeoutException.class, BadGatewayException.class})
    public Optional<Product> ofId(ProductId productId) {
        ProductResponse productResponse;

        log.info("Loading product with id {}", productId);

        try {
            productResponse = productCatalogApiClient.getById(productId.value());
        } catch (ResourceAccessException ex) {
            throw new GatewayTimeoutException("Product Catalog API Timeout", ex);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
			if (ex.getCause() instanceof ResourceAccessException) {
				throw new GatewayTimeoutException("Product Catalog API Timeout", ex);
			}

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
