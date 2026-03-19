package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResilientProductCatalogApiClient {
	private final ProductCatalogApiClient productCatalogApiClient;

	@Cacheable(cacheNames = "algashop:product-catalog-api:v1", key = "#productId")
	@ConcurrencyLimit(10)
	@Retryable(maxRetries = 3, delayString = "3s", multiplier = 2, includes = {GatewayTimeoutException.class, BadGatewayException.class})
	public Optional<ProductResponse> getById(UUID productId) {
        log.info("Getting product by id {}", productId);

		try {
			return Optional.ofNullable(productCatalogApiClient.getById(productId));
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
	}
}