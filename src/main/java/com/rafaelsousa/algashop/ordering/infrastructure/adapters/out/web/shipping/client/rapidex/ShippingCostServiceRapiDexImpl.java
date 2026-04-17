package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.BadGatewayException;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
public class ShippingCostServiceRapiDexImpl implements ShippingCostService {
    private final ResilientRapiDexApiClient rapiDexApiClient;

    @Override
    public CalculationResponse calculate(CalculationRequest request) {
        DeliveryCostResponse deliveryCostResponse;

	    try {
		    deliveryCostResponse = rapiDexApiClient.calculate(DeliveryCostRequest.builder()
	                .originZipCode(request.origin().value())
	                .destinationZipCode(request.destination().value())
	                .build());
	    } catch (ResourceAccessException ex) {
            throw new GatewayTimeoutException("Rapidex API Timeout", ex);
	    } catch (RestClientException ex) {
			if (ex.getCause() instanceof SocketTimeoutException) {
				throw new GatewayTimeoutException("Rapidex API Timeout", ex);
			}

			throw new BadGatewayException("Rapidex API Bad Gateway", ex);
	    }

	    LocalDate expectedDeliveryDate = LocalDate.now().plusDays(deliveryCostResponse.estimatedDaysToDeliver());

        return CalculationResponse.builder()
                .cost(Money.of(deliveryCostResponse.deliveryCost()))
                .expectedDate(expectedDeliveryDate)
                .build();
    }
}