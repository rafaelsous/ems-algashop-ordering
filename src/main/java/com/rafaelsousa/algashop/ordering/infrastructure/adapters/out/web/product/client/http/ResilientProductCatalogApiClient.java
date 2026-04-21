package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.UUID;

import com.rafaelsousa.algashop.ordering.infrastructure.config.resilience.SpringCircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfig;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.core.retry.RetryException;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class ResilientProductCatalogApiClient {
	private final ProductCatalogApiClient productCatalogApiClient;
	private final FrameworkRetryCircuitBreaker circuitBreaker;

	public ResilientProductCatalogApiClient(CircuitBreakerFactory<FrameworkRetryConfig,
	                                        FrameworkRetryConfigBuilder> circuitBreakerFactory,
	                                        ProductCatalogApiClient productCatalogApiClient) {
		this.productCatalogApiClient = productCatalogApiClient;
		this.circuitBreaker = (FrameworkRetryCircuitBreaker) circuitBreakerFactory
				.create(SpringCircuitBreakerConfig.PRODUCT_CATALOG_API_CB_ID);
	}

	@Cacheable(cacheNames = "algashop:product-catalog-api:v1", key = "#productId", unless="#result == null")
	@ConcurrencyLimit(10)
	public Optional<ProductResponse> getById(UUID productId) {
		log.info("Trying to load product by id {}", productId);
		log.info("Product Catalog API CB state is {}", circuitBreaker.getCircuitBreakerPolicy() != null
				? circuitBreaker.getCircuitBreakerPolicy().getState()
				: null
		);

		try {
			return circuitBreaker.run(() -> getProductResponse(productId));
		} catch (NoFallbackAvailableException ex) {
			throw unwrapException(ex);
		}
	}

	private Optional<ProductResponse> getProductResponse(UUID productId) {
		log.info("Getting product response by id {}", productId);

		try {
			return Optional.ofNullable(productCatalogApiClient.getById(productId));
		} catch (HttpClientErrorException.NotFound ex) {
			return Optional.empty();
		} catch (RestClientException ex) {
			throw translateException(ex);
		}
	}

	private RuntimeException translateException(RestClientException ex) {
		if (ex.getCause() instanceof SocketTimeoutException || ex instanceof ResourceAccessException) {
			return new GatewayTimeoutException("Product Catalog API Timeout", ex);
		}

		if (ex instanceof HttpClientErrorException) {
			return new BadGatewayException.ClientErrorException("Product Catalog API Bad Gateway", ex);
		}

		if (ex instanceof HttpServerErrorException) {
			return new BadGatewayException.ServerErrorException("Product Catalog Bad Gateway", ex);
		}

		return new BadGatewayException("Product Catalog Bad Gateway", ex);
	}

	private RuntimeException unwrapException(NoFallbackAvailableException ex) {
		if (ex.getCause() instanceof RetryException re) {
			if (re.getCause() instanceof GatewayTimeoutException gte) {
				return gte;
			}

			if (re.getCause() instanceof BadGatewayException bge) {
				return bge;
			}
		}

		return ex;
	}
}