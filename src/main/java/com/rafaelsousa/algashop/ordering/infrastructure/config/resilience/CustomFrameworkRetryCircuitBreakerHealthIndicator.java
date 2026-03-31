package com.rafaelsousa.algashop.ordering.infrastructure.config.resilience;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.cloud.circuitbreaker.retry.CircuitBreakerRetryPolicy;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfig;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("circuitBreakers")
public class CustomFrameworkRetryCircuitBreakerHealthIndicator implements HealthIndicator {
	private final List<FrameworkRetryCircuitBreaker> circuitBreakers = new ArrayList<>();

	private Throwable lastException = null;
	private String indicatorStatus = "UP";

	public CustomFrameworkRetryCircuitBreakerHealthIndicator(CircuitBreakerFactory<FrameworkRetryConfig,
			FrameworkRetryConfigBuilder> circuitBreakerFactory) {
		circuitBreakers.addAll(List.of(
			(FrameworkRetryCircuitBreaker) circuitBreakerFactory.create(SpringCircuitBreakerConfig.PRODUCT_CATALOG_API_CB_ID),
			(FrameworkRetryCircuitBreaker) circuitBreakerFactory.create(SpringCircuitBreakerConfig.RAPIDEX_API_CB_ID)
		));
	}

	@Override
	public @Nullable Health health() {
		Map<String, Object> indicatorDetails = new HashMap<>();

		for (FrameworkRetryCircuitBreaker circuitBreaker : circuitBreakers) {
			CircuitBreakerRetryPolicy policy = circuitBreaker.getConfig().getCircuitBreakerRetryPolicy();
			Map<String, Object> cbDetails = new HashMap<>();

			processPolicy(policy, cbDetails);

			indicatorDetails.put(circuitBreaker.getId(), cbDetails);
		}

		Health.Builder builder = Health.status(indicatorStatus).withDetails(indicatorDetails);

		if (indicatorStatus.equals("DEGRADED") && lastException != null) {
			builder.withException(lastException);
		}

		return builder.build();
	}

	private void  processPolicy(CircuitBreakerRetryPolicy policy, Map<String, Object> cbDetails) {
		CircuitBreakerRetryPolicy.State state = policy != null ? policy.getState() : null;

		if (state != null) {
			cbDetails.put("state", state.name());

			if (state == CircuitBreakerRetryPolicy.State.OPEN) {
				indicatorStatus = "DEGRADED";
				Throwable exception = policy.getLastException();
				Throwable cause = exception != null ? exception.getCause() : null;

				if (cause != null) {
					lastException = cause;
					cbDetails.put("lastException", cause.getMessage());
				} else {
					cbDetails.put("lastException", null);
				}
			}
		}
	}
}