package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
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

import java.net.SocketTimeoutException;

@Slf4j
@Component
public class ResilientRapiDexApiClient {
	private final RapiDexApiClient rapiDexApiClient;
	private final FrameworkRetryCircuitBreaker circuitBreaker;

	public ResilientRapiDexApiClient(CircuitBreakerFactory<FrameworkRetryConfig,
			FrameworkRetryConfigBuilder> circuitBreakerFactory, RapiDexApiClient rapiDexApiClient) {
		this.rapiDexApiClient = rapiDexApiClient;
		this.circuitBreaker = (FrameworkRetryCircuitBreaker) circuitBreakerFactory.create("rapidexAPICB");
	}

	@ConcurrencyLimit(10)
	public DeliveryCostResponse calculate(DeliveryCostRequest deliveryCostRequest) {
		log.info("Trying to calculate shipping cost to {}", deliveryCostRequest.destinationZipCode());
		log.info("Rapidex API CB state is {}", circuitBreaker.getCircuitBreakerPolicy() != null
				? circuitBreaker.getCircuitBreakerPolicy().getState()
				: null
		);

		try {
			return circuitBreaker.run(
					() -> processCalculation(deliveryCostRequest),
					ex -> doInternalFallback(deliveryCostRequest, ex)
			);
		} catch (NoFallbackAvailableException ex) {
			throw unwrapException(ex);
		}
	}

	private DeliveryCostResponse processCalculation(DeliveryCostRequest deliveryCostRequest) {
		try {
			log.info("Calculating shipping cost to {}", deliveryCostRequest.destinationZipCode());
			return rapiDexApiClient.calculate(deliveryCostRequest);
		} catch (HttpClientErrorException ex) {
			if (!(ex instanceof HttpClientErrorException.NotFound)) {
				log.error("Client HTTP error when loading delivery cost {}", deliveryCostRequest, ex);
			}

			return null;
		} catch (RestClientException ex) {
			throw translateException(ex);
		}
	}

	private DeliveryCostResponse doInternalFallback(DeliveryCostRequest deliveryCostRequest, Throwable ex) {
		log.info("Rapidex API fallback for request {}", deliveryCostRequest);

		// Alternative logic
		return DeliveryCostResponse.builder()
				.deliveryCost("20.0")
				.estimatedDaysToDeliver(10L)
				.build();
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

	private RuntimeException translateException(RestClientException ex) {
		if (ex.getCause() instanceof SocketTimeoutException || ex instanceof ResourceAccessException) {
			return new GatewayTimeoutException("Rapidex API Timeout", ex);
		}

		if (ex instanceof HttpClientErrorException) {
			return new BadGatewayException.ClientErrorException("Rapidex API Bad Gateway", ex);
		}

		if (ex instanceof HttpServerErrorException) {
			return new BadGatewayException.ServerErrorException("Rapidex Bad Gateway", ex);
		}

		return new BadGatewayException("Rapidex Bad Gateway", ex);
	}
}